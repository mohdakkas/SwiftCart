package com.swiftcart.dto;

import com.swiftcart.dto.AddressDtos.AddressResponse;
import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record PlaceOrderRequest(@NotNull Long shippingAddressId, @NotNull Long billingAddressId, @NotNull PaymentMethod paymentMethod, String couponCode) {
    }

    public record OrderLineResponse(Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
    }

    public record PaymentResponse(String transactionId, PaymentMethod method, PaymentStatus status, BigDecimal amount, Instant paymentDate) {
    }

    public record OrderResponse(String orderNumber, OrderStatus status, BigDecimal totalAmount, List<OrderLineResponse> items, PaymentResponse payment, AddressResponse shippingAddress) {
    }

    public record OrderTrackingResponse(OrderStatus status, String message, Instant updatedAt, boolean completed, boolean current) {
    }

    public record OrderTrackingTimelineResponse(String orderNumber, OrderStatus currentStatus, List<OrderTrackingResponse> history, AddressResponse shippingAddress) {
    }

    public record UpdateOrderStatusRequest(@NotNull OrderStatus status, String message) {
    }
}
