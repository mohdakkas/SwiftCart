package com.swiftcart.service;

import com.swiftcart.dto.AuthDtos.AuthResponse;
import com.swiftcart.dto.AuthDtos.ForgotPasswordRequest;
import com.swiftcart.dto.AuthDtos.LoginRequest;
import com.swiftcart.dto.AuthDtos.LogoutRequest;
import com.swiftcart.dto.AuthDtos.RefreshRequest;
import com.swiftcart.dto.AuthDtos.RegisterRequest;
import com.swiftcart.dto.AuthDtos.ResetPasswordRequest;
import com.swiftcart.dto.AuthDtos.VerifyEmailRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshRequest request);
    void logout(String email, LogoutRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void verifyEmail(VerifyEmailRequest request);
}
