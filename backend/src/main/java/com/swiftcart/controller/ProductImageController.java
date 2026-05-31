package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.ProductImageDtos.ProductImageResponse;
import com.swiftcart.service.ProductImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
public class ProductImageController {
    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductImageResponse> uploadMainImage(@PathVariable Long productId, @RequestPart("file") MultipartFile file) {
        return ApiResponse.ok("Main product image uploaded", productImageService.uploadMainImage(productId, file));
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<ProductImageResponse>> uploadGalleryImages(@PathVariable Long productId, @RequestPart("files") List<MultipartFile> files) {
        return ApiResponse.ok("Product gallery images uploaded", productImageService.uploadGalleryImages(productId, files));
    }

    @DeleteMapping("/images/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ApiResponse.ok("Product image deleted", null);
    }
}
