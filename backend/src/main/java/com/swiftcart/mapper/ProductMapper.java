package com.swiftcart.mapper;

import com.swiftcart.dto.ProductImageDtos.ProductImageResponse;
import com.swiftcart.dto.ProductDtos.ProductResponse;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getBrand(),
                product.getSlug(),
                product.getDescription(),
                product.getPrice(),
                product.getSalePrice(),
                product.getStockQuantity(),
                product.getAverageRating(),
                product.getReviewCount(),
                product.isFeatured(),
                product.isTrending(),
                product.isBestSeller(),
                product.isNewArrival(),
                product.getBadge(),
                product.getImageUrl(),
                product.getCategory().getName(),
                product.getImages().stream().map(ProductImage::getImageUrl).toList(),
                product.getImages().stream()
                        .map(image -> new ProductImageResponse(image.getId(), product.getId(), image.getImageUrl(), image.isMainImage()))
                        .toList()
        );
    }
}



