package com.swiftcart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public final class CartDtos {
    private CartDtos() {
    }

    public record AddCartItemRequest(@NotNull Long productId, @Min(1) int quantity) {
    }

    public record UpdateCartItemRequest(@Min(1) int quantity) {
    }

    public record CartItemResponse(Long id, Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
    }

    public record CartResponse(List<CartItemResponse> items, BigDecimal subtotal, BigDecimal tax, BigDecimal shipping, BigDecimal total) {
    }
}
