package com.swiftcart.service.impl;

import com.swiftcart.dto.ProductImageDtos.ProductImageResponse;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.ProductImage;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.ProductImageRepository;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.service.FileUploadService;
import com.swiftcart.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileUploadService fileUploadService;

    public ProductImageServiceImpl(ProductRepository productRepository, ProductImageRepository productImageRepository, FileUploadService fileUploadService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public ProductImageResponse uploadMainImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        FileUploadService.UploadedFile uploadedFile = fileUploadService.storeProductImage(file);
        clearMainImage(product);
        product.setImageUrl(uploadedFile.url());
        product.setImagePublicId(uploadedFile.publicId());

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(uploadedFile.url());
        image.setImagePublicId(uploadedFile.publicId());
        image.setMainImage(true);
        image.setPrimaryImage(true);
        image.setSortOrder(0);
        product.getImages().add(image);
        return toResponse(productImageRepository.save(image));
    }

    @Override
    public List<ProductImageResponse> uploadGalleryImages(Long productId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("At least one gallery image is required");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        int nextSort = product.getImages().stream().mapToInt(ProductImage::getSortOrder).max().orElse(0) + 1;
        return files.stream().map(file -> {
            FileUploadService.UploadedFile uploadedFile = fileUploadService.storeProductImage(file);
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(uploadedFile.url());
            image.setImagePublicId(uploadedFile.publicId());
            image.setMainImage(false);
            image.setPrimaryImage(false);
            image.setSortOrder(nextSort + product.getImages().size());
            product.getImages().add(image);
            return toResponse(productImageRepository.save(image));
        }).toList();
    }

    @Override
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found"));
        Product product = image.getProduct();
        boolean wasMainImage = image.isMainImage();
        String publicId = image.getImagePublicId();
        product.getImages().remove(image);
        productImageRepository.delete(image);
        fileUploadService.delete(publicId);
        if (wasMainImage) {
            product.setImageUrl(null);
            product.setImagePublicId(null);
            product.getImages().stream().findFirst().ifPresent(nextImage -> {
                nextImage.setMainImage(true);
                nextImage.setPrimaryImage(true);
                product.setImageUrl(nextImage.getImageUrl());
                product.setImagePublicId(nextImage.getImagePublicId());
            });
        }
    }

    private void clearMainImage(Product product) {
        product.getImages().forEach(image -> {
            image.setMainImage(false);
            image.setPrimaryImage(false);
        });
    }

    private ProductImageResponse toResponse(ProductImage image) {
        return new ProductImageResponse(image.getId(), image.getProduct().getId(), image.getImageUrl(), image.isMainImage());
    }
}
