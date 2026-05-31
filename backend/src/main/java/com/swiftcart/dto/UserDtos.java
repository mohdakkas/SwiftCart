package com.swiftcart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public final class UserDtos {
    private UserDtos() {
    }

    public record ProfileResponse(
            Long id,
            String email,
            String firstName,
            String lastName,
            String phone,
            String profileImageUrl,
            boolean emailVerified,
            Set<String> roles
    ) {
    }

    public record UpdateProfileRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,
            String profileImageUrl
    ) {
    }

    public record ChangePasswordRequest(
            @NotBlank String oldPassword,
            @NotBlank @Size(min = 8, max = 80) String newPassword
    ) {
    }

    public record UploadProfileImageResponse(String profileImageUrl) {
    }
}
