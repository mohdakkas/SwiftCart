package com.swiftcart.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public final class WishlistDtos {
    private WishlistDtos() {
    }

    public record AddWishlistItemRequest(@NotNull Long productId) {
    }

    public record WishlistItemResponse(Long id, Long productId, String productName, BigDecimal price, String imageUrl) {
    }

    public record WishlistResponse(List<WishlistItemResponse> items) {
    }
}
