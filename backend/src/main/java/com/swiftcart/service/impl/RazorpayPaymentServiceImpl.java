package com.swiftcart.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftcart.config.RazorpayProperties;
import com.swiftcart.dto.AddressDtos.AddressResponse;
import com.swiftcart.dto.OrderDtos.OrderLineResponse;
import com.swiftcart.dto.OrderDtos.OrderResponse;
import com.swiftcart.dto.OrderDtos.PaymentResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayFailureRequest;
import com.swiftcart.dto.PaymentDtos.RazorpayOrderRequest;
import com.swiftcart.dto.PaymentDtos.RazorpayOrderResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayPaymentResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayVerifyRequest;
import com.swiftcart.entity.Address;
import com.swiftcart.entity.Cart;
import com.swiftcart.entity.Order;
import com.swiftcart.entity.OrderItem;
import com.swiftcart.entity.OrderTracking;
import com.swiftcart.entity.Payment;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.User;
import com.swiftcart.enums.AddressType;
import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.AddressRepository;
import com.swiftcart.repository.CartRepository;
import com.swiftcart.repository.OrderRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.RazorpayPaymentService;
import com.swiftcart.util.TransactionIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
@Transactional
public class RazorpayPaymentServiceImpl implements RazorpayPaymentService {
    private final RazorpayProperties razorpayProperties;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public RazorpayPaymentServiceImpl(RazorpayProperties razorpayProperties,
                                      ObjectMapper objectMapper,
                                      UserRepository userRepository,
                                      CartRepository cartRepository,
                                      AddressRepository addressRepository,
                                      OrderRepository orderRepository) {
        this.razorpayProperties = razorpayProperties;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public RazorpayOrderResponse createOrder(String email, RazorpayOrderRequest request) {
        validateRazorpayConfig();
        validateRazorpayMethod(request.paymentMethod());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUserEmail(email).orElseThrow(() -> new BusinessException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        Address shipping = addressRepository.findByIdAndUserEmail(request.shippingAddressId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
        Address billing = addressRepository.findByIdAndUserEmail(request.billingAddressId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));

        Order order = buildPendingOrder(user, cart, shipping, billing, request);
        Order savedOrder = orderRepository.save(order);
        String razorpayOrderId = createRazorpayOrder(savedOrder.getOrderNumber(), savedOrder.getTotalAmount());

        Payment payment = savedOrder.getPayment();
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setTransactionId(razorpayOrderId);
        payment.setGatewayMessage("Razorpay order created. Waiting for payment verification.");

        return new RazorpayOrderResponse(
                razorpayProperties.getKeyId(),
                razorpayOrderId,
                savedOrder.getOrderNumber(),
                savedOrder.getTotalAmount(),
                toPaise(savedOrder.getTotalAmount()),
                razorpayProperties.getCurrency(),
                fullName(user),
                user.getEmail(),
                user.getPhone()
        );
    }

    @Override
    public OrderResponse verifyPayment(String email, RazorpayVerifyRequest request) {
        Order order = orderRepository.findByOrderNumber(request.swiftCartOrderNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        ensureOrderOwner(email, order);

        Payment payment = order.getPayment();
        if (payment == null) {
            throw new BusinessException("Payment not found for order");
        }
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return toResponse(order);
        }
        if (!request.razorpayOrderId().equals(payment.getRazorpayOrderId())) {
            throw new BusinessException("Razorpay order mismatch");
        }
        if (!isValidSignature(request.razorpayOrderId(), request.razorpayPaymentId(), request.razorpaySignature())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayMessage("Razorpay signature verification failed");
            throw new BusinessException("Payment verification failed");
        }

        reduceStock(order);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(request.razorpayPaymentId());
        payment.setRazorpayPaymentId(request.razorpayPaymentId());
        payment.setRazorpaySignature(request.razorpaySignature());
        payment.setPaymentDate(Instant.now());
        payment.setGatewayMessage("Razorpay payment verified successfully");
        clearUserCart(email);
        return toResponse(order);
    }

    @Override
    public RazorpayPaymentResponse markFailed(String email, RazorpayFailureRequest request) {
        Order order = orderRepository.findByOrderNumber(request.swiftCartOrderNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        ensureOrderOwner(email, order);
        Payment payment = order.getPayment();
        if (payment == null) {
            throw new BusinessException("Payment not found for order");
        }
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentDate(Instant.now());
            payment.setGatewayMessage(request.reason() == null || request.reason().isBlank() ? "Razorpay payment failed or was cancelled" : request.reason().trim());
        }
        return new RazorpayPaymentResponse(order.getOrderNumber(), payment.getRazorpayOrderId(), payment.getRazorpayPaymentId(), payment.getStatus(), payment.getAmount());
    }

    private Order buildPendingOrder(User user, Cart cart, Address shipping, Address billing, RazorpayOrderRequest request) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);
        order.setOrderNumber("SC-" + System.currentTimeMillis());
        order.setStatus(OrderStatus.PLACED);

        cart.getItems().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException(product.getName() + " has only " + product.getStockQuantity() + " item(s) left in stock");
            }
            OrderItem line = new OrderItem();
            line.setOrder(order);
            line.setProduct(product);
            line.setProductName(product.getName());
            line.setQuantity(cartItem.getQuantity());
            line.setUnitPrice(cartItem.getUnitPrice());
            line.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            order.getItems().add(line);
        });

        BigDecimal subtotal = order.getItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = "WELCOME10".equalsIgnoreCase(request.couponCode()) ? subtotal.multiply(new BigDecimal("0.10")) : BigDecimal.ZERO;
        BigDecimal tax = subtotal.subtract(discount).multiply(new BigDecimal("0.18"));
        BigDecimal shippingFee = subtotal.compareTo(new BigDecimal("999")) > 0 ? BigDecimal.ZERO : new BigDecimal("99");
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTaxAmount(tax);
        order.setShippingAmount(shippingFee);
        order.setTotalAmount(subtotal.subtract(discount).add(tax).add(shippingFee));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.paymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId("RZP-PENDING-" + TransactionIdGenerator.next());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setGatewayMessage("Razorpay payment pending");
        order.setPayment(payment);
        addTracking(order, OrderStatus.PLACED, "Order created and waiting for Razorpay payment");
        return order;
    }

    private String createRazorpayOrder(String receipt, BigDecimal amount) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "amount", toPaise(amount),
                    "currency", razorpayProperties.getCurrency(),
                    "receipt", receipt,
                    "payment_capture", 1
            ));
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1/orders"))
                    .header("Authorization", basicAuth())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException("Razorpay order creation failed: " + response.body());
            }
            JsonNode json = objectMapper.readTree(response.body());
            return json.path("id").asText();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Could not connect to Razorpay. Check internet connection and Razorpay keys.");
        }
    }

    private void reduceStock(Order order) {
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException(product.getName() + " has only " + product.getStockQuantity() + " item(s) left in stock");
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
        });
    }

    private void clearUserCart(String email) {
        cartRepository.findByUserEmail(email).ifPresent(cart -> cart.getItems().clear());
    }

    private void addTracking(Order order, OrderStatus status, String message) {
        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(order);
        tracking.setStatus(status);
        tracking.setMessage(message);
        order.getTrackingHistory().add(tracking);
    }

    private boolean isValidSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayProperties.getKeySecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = toHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), razorpaySignature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new BusinessException("Could not verify Razorpay signature");
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }

    private long toPaise(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private String basicAuth() {
        String raw = razorpayProperties.getKeyId() + ":" + razorpayProperties.getKeySecret();
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private void validateRazorpayConfig() {
        if (!razorpayProperties.isConfigured()) {
            throw new BusinessException("Add Razorpay test Key ID and Secret in application.properties or environment variables before using Razorpay checkout");
        }
    }

    private void validateRazorpayMethod(PaymentMethod method) {
        if (method != PaymentMethod.UPI && method != PaymentMethod.CREDIT_CARD && method != PaymentMethod.DEBIT_CARD) {
            throw new BusinessException("Razorpay checkout supports UPI and card payments only");
        }
    }

    private void ensureOrderOwner(String email, Order order) {
        if (!order.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("You can access only your own payment");
        }
    }

    private OrderResponse toResponse(Order order) {
        var lines = order.getItems().stream()
                .map(line -> new OrderLineResponse(line.getProduct().getId(), line.getProductName(), line.getQuantity(), line.getUnitPrice(), line.getTotalPrice()))
                .toList();
        Payment payment = order.getPayment();
        PaymentResponse paymentResponse = payment == null ? null : new PaymentResponse(payment.getTransactionId(), payment.getMethod(), payment.getStatus(), payment.getAmount(), payment.getPaymentDate());
        return new OrderResponse(order.getOrderNumber(), order.getStatus(), order.getTotalAmount(), lines, paymentResponse, toAddressResponse(order.getShippingAddress()));
    }

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                firstNonBlank(address.getFullName(), address.getRecipientName()),
                firstNonBlank(address.getPhoneNumber(), address.getLegacyPhone()),
                firstNonBlank(address.getAddressLine1(), address.getLegacyLine1()),
                firstNonBlank(address.getAddressLine2(), address.getLegacyLine2()),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getAddressType() == null ? fromLabel(address.getLabel()) : address.getAddressType(),
                address.isDefaultAddress() || address.isLegacyDefaultAddress()
        );
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }

    private AddressType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return AddressType.HOME;
        }
        return switch (label.trim().toUpperCase()) {
            case "OFFICE" -> AddressType.OFFICE;
            case "OTHER" -> AddressType.OTHER;
            default -> AddressType.HOME;
        };
    }

    private String fullName(User user) {
        return (firstNonBlank(user.getFirstName(), "") + " " + firstNonBlank(user.getLastName(), "")).trim();
    }
}
