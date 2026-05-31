package com.swiftcart.dto;

import com.swiftcart.dto.ProductImageDtos.ProductImageResponse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public final class ProductDtos {
    private ProductDtos() {
    }

    public record ProductResponse(
            Long id,
            String sku,
            String name,
            String slug,
            String description,
            BigDecimal price,
            BigDecimal salePrice,
            int stockQuantity,
            BigDecimal averageRating,
            int reviewCount,
            boolean featured,
            boolean trending,
            boolean bestSeller,
            boolean newArrival,
            String badge,
            String imageUrl,
            String category,
            List<String> images,
            List<ProductImageResponse> productImages
    ) {
    }

    public record ProductRequest(
            @NotNull Long categoryId,
            @NotBlank String sku,
            @NotBlank String name,
            @NotBlank String slug,
            @NotBlank String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            BigDecimal salePrice,
            @Min(0) int stockQuantity,
            Boolean featured,
            Boolean trending,
            Boolean bestSeller,
            Boolean newArrival,
            String badge,
            List<String> images
    ) {
    }

    public record ProductUpdateRequest(
            Long categoryId,
            String sku,
            String name,
            String slug,
            String description,
            BigDecimal price,
            BigDecimal salePrice,
            @Min(0) Integer stockQuantity,
            Boolean featured,
            Boolean trending,
            Boolean bestSeller,
            Boolean newArrival,
            String badge,
            Boolean active,
            List<String> images
    ) {
    }
}
