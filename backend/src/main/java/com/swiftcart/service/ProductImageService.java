package com.swiftcart.service;

import com.swiftcart.dto.ProductImageDtos.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    ProductImageResponse uploadMainImage(Long productId, MultipartFile file);

    List<ProductImageResponse> uploadGalleryImages(Long productId, List<MultipartFile> files);

    void deleteImage(Long imageId);
}
