package com.swiftcart.dto;

import jakarta.validation.constraints.NotBlank;

public final class CategoryDtos {
    private CategoryDtos() {
    }

    public record CategoryRequest(@NotBlank String name, @NotBlank String slug, String imageUrl, Long parentId, Boolean active) {
    }

    public record CategoryResponse(Long id, String name, String slug, String imageUrl, Long parentId, boolean active) {
    }
}
