package com.swiftcart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class ReviewDtos {
    private ReviewDtos() {
    }

    public record ReviewRequest(
            @NotNull Long productId,
            @Min(1) @Max(5) int rating,
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 4000) String comment
    ) {
    }

    public record ReviewUpdateRequest(
            @Min(1) @Max(5) int rating,
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 4000) String comment
    ) {
    }

    public record ReviewResponse(
            Long id,
            int rating,
            String title,
            String comment,
            Instant createdAt,
            Long userId,
            String userName,
            String userEmail,
            Long productId
    ) {
    }
}
