package com.swiftcart.service;

import com.swiftcart.dto.OrderDtos.OrderResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayFailureRequest;
import com.swiftcart.dto.PaymentDtos.RazorpayOrderRequest;
import com.swiftcart.dto.PaymentDtos.RazorpayOrderResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayPaymentResponse;
import com.swiftcart.dto.PaymentDtos.RazorpayVerifyRequest;

public interface RazorpayPaymentService {
    RazorpayOrderResponse createOrder(String email, RazorpayOrderRequest request);
    OrderResponse verifyPayment(String email, RazorpayVerifyRequest request);
    RazorpayPaymentResponse markFailed(String email, RazorpayFailureRequest request);
}
