package com.swiftcart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RazorpayProperties {
    @Value("${razorpay.key-id:rzp_test_replace_me}")
    private String keyId;

    @Value("${razorpay.key-secret:replace_me}")
    private String keySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    public String getKeyId() {
        return keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isConfigured() {
        return keyId != null && !keyId.isBlank() && !"rzp_test_replace_me".equals(keyId)
                && keySecret != null && !keySecret.isBlank() && !"replace_me".equals(keySecret);
    }
}
