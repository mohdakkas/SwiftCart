package com.swiftcart.dto;

import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class PaymentDtos {
    private PaymentDtos() {
    }

    public record RazorpayOrderRequest(
            @NotNull Long shippingAddressId,
            @NotNull Long billingAddressId,
            @NotNull PaymentMethod paymentMethod,
            String couponCode
    ) {
    }

    public record RazorpayOrderResponse(
            String keyId,
            String razorpayOrderId,
            String swiftCartOrderNumber,
            BigDecimal amount,
            long amountInPaise,
            String currency,
            String customerName,
            String customerEmail,
            String customerPhone
    ) {
    }

    public record RazorpayVerifyRequest(
            @NotBlank String swiftCartOrderNumber,
            @NotBlank String razorpayOrderId,
            @NotBlank String razorpayPaymentId,
            @NotBlank String razorpaySignature
    ) {
    }

    public record RazorpayFailureRequest(
            @NotBlank String swiftCartOrderNumber,
            String reason
    ) {
    }

    public record RazorpayPaymentResponse(
            String swiftCartOrderNumber,
            String razorpayOrderId,
            String razorpayPaymentId,
            PaymentStatus paymentStatus,
            BigDecimal amount
    ) {
    }
}
