package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.util.TransactionIdGenerator;
import jakarta.validation.constraints.NotNull;
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
    public record PaymentSimulationRequest(@NotNull PaymentMethod method, @NotNull BigDecimal amount, boolean forceFailure) {
    }

    @PostMapping("/simulate")
    public ApiResponse<Map<String, Object>> simulate(@RequestBody PaymentSimulationRequest request) {
        PaymentStatus status = request.forceFailure() ? PaymentStatus.FAILED : PaymentStatus.SUCCESS;
        return ApiResponse.ok("Dummy payment gateway response",
                Map.of("transactionId", TransactionIdGenerator.next(), "method", request.method(), "amount", request.amount(), "status", status, "paymentDate", Instant.now()));
    }

    @PostMapping("/manage/reconcile")
    public ApiResponse<Map<String, Object>> reconcile() {
        return ApiResponse.ok("Payment management access granted", Map.of("status", "READY"));
    }
}
