package com.swiftcart.dto;

public final class ProductImageDtos {
    private ProductImageDtos() {
    }

    public record ProductImageResponse(
            Long id,
            Long productId,
            String imageUrl,
            boolean mainImage
    ) {
    }
}
