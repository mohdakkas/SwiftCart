package com.swiftcart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 80) String password,
            @NotBlank @Size(min = 8, max = 80) String confirmPassword,
            String firstName,
            String lastName,
            String phone
    ) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record LogoutRequest(@NotBlank String refreshToken) {
    }

    public record ForgotPasswordRequest(@NotBlank @Email String email) {
    }

    public record ResetPasswordRequest(@NotBlank String token, @NotBlank @Size(min = 8, max = 80) String newPassword) {
    }

    public record VerifyEmailRequest(@NotBlank String token) {
    }

    public record AuthResponse(String accessToken, String refreshToken, String role, UserSummary user) {
    }

    public record UserSummary(Long id, String email, String fullName, String firstName, String lastName, String role, Set<String> roles) {
    }
}
