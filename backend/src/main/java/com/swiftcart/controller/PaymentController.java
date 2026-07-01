package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.OrderDtos.OrderResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayFailureRequest;
import com.swiftcart.dto.PaymentDtos.RazorpayOrderRequest;
import com.swiftcart.dto.PaymentDtos.RazorpayOrderResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayPaymentResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayVerifyRequest;
import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.service.RazorpayPaymentService;
import com.swiftcart.util.TransactionIdGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final RazorpayPaymentService razorpayPaymentService;

    public PaymentController(RazorpayPaymentService razorpayPaymentService) {
        this.razorpayPaymentService = razorpayPaymentService;
    }

    public record PaymentSimulationRequest(@NotNull PaymentMethod method, @NotNull BigDecimal amount, boolean forceFailure) {
    }

    @PostMapping("/simulate")
    public ApiResponse<Map<String, Object>> simulate(@RequestBody PaymentSimulationRequest request) {
        PaymentStatus status = request.forceFailure() ? PaymentStatus.FAILED : PaymentStatus.SUCCESS;
        return ApiResponse.ok("Dummy payment gateway response",
                Map.of("transactionId", TransactionIdGenerator.next(), "method", request.method(), "amount", request.amount(), "status", status, "paymentDate", Instant.now()));
    }

    @PostMapping("/razorpay/order")
    public ApiResponse<RazorpayOrderResponse> createRazorpayOrder(Authentication authentication,
                                                                  @Valid @RequestBody RazorpayOrderRequest request) {
        return ApiResponse.ok("Razorpay order created", razorpayPaymentService.createOrder(authentication.getName(), request));
    }

    @PostMapping("/razorpay/verify")
    public ApiResponse<OrderResponse> verifyRazorpayPayment(Authentication authentication,
                                                            @Valid @RequestBody RazorpayVerifyRequest request) {
        return ApiResponse.ok("Razorpay payment verified", razorpayPaymentService.verifyPayment(authentication.getName(), request));
    }

    @PostMapping("/razorpay/failure")
    public ApiResponse<RazorpayPaymentResponse> markRazorpayFailed(Authentication authentication,
                                                                   @Valid @RequestBody RazorpayFailureRequest request) {
        return ApiResponse.ok("Razorpay payment marked failed", razorpayPaymentService.markFailed(authentication.getName(), request));
    }

    @PostMapping("/manage/reconcile")
    public ApiResponse<Map<String, Object>> reconcile() {
        return ApiResponse.ok("Payment management access granted", Map.of("status", "READY"));
    }
}
