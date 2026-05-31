package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.AuthDtos.AuthResponse;
import com.swiftcart.dto.AuthDtos.ForgotPasswordRequest;
import com.swiftcart.dto.AuthDtos.LoginRequest;
import com.swiftcart.dto.AuthDtos.LogoutRequest;
import com.swiftcart.dto.AuthDtos.RefreshRequest;
import com.swiftcart.dto.AuthDtos.RegisterRequest;
import com.swiftcart.dto.AuthDtos.ResetPasswordRequest;
import com.swiftcart.dto.AuthDtos.VerifyEmailRequest;
import com.swiftcart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Logged in successfully", authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok("Token refreshed", authService.refresh(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshAlias(@Valid @RequestBody RefreshRequest request) {
        return refresh(request);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication authentication, @Valid @RequestBody LogoutRequest request) {
        authService.logout(authentication.getName(), request);
        return ApiResponse.ok("Logged out successfully", null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.accepted().body(ApiResponse.ok("Password reset instructions accepted", null));
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password reset successfully", null);
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ApiResponse.ok("Email verified successfully", null);
    }
}
