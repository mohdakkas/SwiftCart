package com.swiftcart.service.impl;

import com.swiftcart.dto.OrderDtos.OrderLineResponse;
import com.swiftcart.dto.OrderDtos.OrderResponse;
import com.swiftcart.dto.OrderDtos.OrderTrackingResponse;
import com.swiftcart.dto.OrderDtos.OrderTrackingTimelineResponse;
import com.swiftcart.dto.OrderDtos.PaymentResponse;
import com.swiftcart.dto.OrderDtos.PlaceOrderRequest;
import com.swiftcart.dto.OrderDtos.UpdateOrderStatusRequest;
import com.swiftcart.entity.Address;
import com.swiftcart.entity.Cart;
import com.swiftcart.entity.Order;
import com.swiftcart.entity.OrderItem;
import com.swiftcart.entity.OrderTracking;
import com.swiftcart.entity.Payment;
import com.swiftcart.entity.User;
import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.AddressRepository;
import com.swiftcart.repository.CartRepository;
import com.swiftcart.repository.OrderRepository;
import com.swiftcart.repository.OrderTrackingRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.OrderService;
import com.swiftcart.util.TransactionIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final List<OrderStatus> DELIVERY_FLOW = List.of(
            OrderStatus.PLACED,
            OrderStatus.CONFIRMED,
            OrderStatus.SHIPPED,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.DELIVERED
    );
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PLACED, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, Set.of(OrderStatus.OUT_FOR_DELIVERY));
        ALLOWED_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of(OrderStatus.RETURNED));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of(OrderStatus.REFUNDED));
        ALLOWED_TRANSITIONS.put(OrderStatus.RETURNED, Set.of(OrderStatus.REFUNDED));
        ALLOWED_TRANSITIONS.put(OrderStatus.REFUNDED, Set.of());
    }

    private final OrderRepository orderRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderTrackingRepository orderTrackingRepository, UserRepository userRepository, CartRepository cartRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.orderTrackingRepository = orderTrackingRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public OrderResponse place(String email, PlaceOrderRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUserEmail(email).orElseThrow(() -> new BusinessException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }
        Address shipping = addressRepository.findById(request.shippingAddressId()).orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
        Address billing = addressRepository.findById(request.billingAddressId()).orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shipping);
        order.setBillingAddress(billing);
        order.setOrderNumber("SC-" + System.currentTimeMillis());
        order.setStatus(OrderStatus.PLACED);

        cart.getItems().forEach(cartItem -> {
            OrderItem line = new OrderItem();
            line.setOrder(order);
            line.setProduct(cartItem.getProduct());
            line.setProductName(cartItem.getProduct().getName());
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
        payment.setTransactionId(TransactionIdGenerator.next());
        payment.setStatus(request.paymentMethod().name().equals("CASH_ON_DELIVERY") ? PaymentStatus.PENDING : PaymentStatus.SUCCESS);
        payment.setPaymentDate(Instant.now());
        payment.setGatewayMessage("Dummy gateway response generated by SwiftCart");
        order.setPayment(payment);
        addTracking(order, OrderStatus.PLACED, "Order placed successfully");

        cart.getItems().clear();
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> history(String email, Pageable pageable) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> manage(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderTrackingTimelineResponse tracking(String email, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!isAdmin(email) && !order.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("You can view only your own order tracking");
        }
        return toTrackingTimeline(order);
    }

    @Override
    public OrderTrackingTimelineResponse updateStatus(String email, String orderNumber, UpdateOrderStatusRequest request) {
        if (!isAdmin(email)) {
            throw new BusinessException("Only admins can update order status");
        }
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        validateTransition(order.getStatus(), request.status());
        order.setStatus(request.status());
        addTracking(order, request.status(), messageFor(request.status(), request.message()));
        return toTrackingTimeline(order);
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            throw new BusinessException("Order is already " + next);
        }
        if (!ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(next)) {
            throw new BusinessException("Invalid order status flow from " + current + " to " + next);
        }
    }

    private void addTracking(Order order, OrderStatus status, String message) {
        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(order);
        tracking.setStatus(status);
        tracking.setMessage(message);
        order.getTrackingHistory().add(tracking);
    }

    private String messageFor(OrderStatus status, String customMessage) {
        if (customMessage != null && !customMessage.isBlank()) {
            return customMessage.trim();
        }
        return switch (status) {
            case PLACED -> "Order placed successfully";
            case CONFIRMED -> "Order confirmed by SwiftCart";
            case SHIPPED -> "Order shipped from the warehouse";
            case OUT_FOR_DELIVERY -> "Order is out for delivery";
            case DELIVERED -> "Order delivered successfully";
            case CANCELLED -> "Order cancelled";
            case RETURNED -> "Order return requested";
            case REFUNDED -> "Refund processed";
        };
    }

    private boolean isAdmin(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .map(user -> user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName())))
                .orElse(false);
    }

    private OrderTrackingTimelineResponse toTrackingTimeline(Order order) {
        List<OrderTracking> storedHistory = orderTrackingRepository.findByOrderOrderNumberOrderByCreatedAtAsc(order.getOrderNumber());
        if (storedHistory.isEmpty()) {
            return new OrderTrackingTimelineResponse(order.getOrderNumber(), order.getStatus(), List.of(
                    new OrderTrackingResponse(order.getStatus(), messageFor(order.getStatus(), null), order.getUpdatedAt(), true, true)
            ));
        }
        List<OrderTrackingResponse> history = storedHistory.stream()
                .map(entry -> new OrderTrackingResponse(
                        entry.getStatus(),
                        entry.getMessage(),
                        entry.getUpdatedAt(),
                        isCompleted(order.getStatus(), entry.getStatus()),
                        order.getStatus() == entry.getStatus()
                ))
                .toList();
        return new OrderTrackingTimelineResponse(order.getOrderNumber(), order.getStatus(), history);
    }

    private boolean isCompleted(OrderStatus current, OrderStatus entryStatus) {
        int currentIndex = DELIVERY_FLOW.indexOf(current);
        int entryIndex = DELIVERY_FLOW.indexOf(entryStatus);
        if (currentIndex >= 0 && entryIndex >= 0) {
            return entryIndex <= currentIndex;
        }
        return current == entryStatus;
    }

    private OrderResponse toResponse(Order order) {
        var lines = order.getItems().stream()
                .map(line -> new OrderLineResponse(line.getProduct().getId(), line.getProductName(), line.getQuantity(), line.getUnitPrice(), line.getTotalPrice()))
                .toList();
        Payment payment = order.getPayment();
        PaymentResponse paymentResponse = payment == null ? null : new PaymentResponse(payment.getTransactionId(), payment.getMethod(), payment.getStatus(), payment.getAmount(), payment.getPaymentDate());
        return new OrderResponse(order.getOrderNumber(), order.getStatus(), order.getTotalAmount(), lines, paymentResponse);
    }
}
