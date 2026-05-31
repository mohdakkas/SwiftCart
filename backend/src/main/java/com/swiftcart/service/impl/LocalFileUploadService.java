package com.swiftcart.service.impl;

import com.swiftcart.exception.BusinessException;
import com.swiftcart.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileUploadService implements FileUploadService {
    private static final long MAX_FILE_SIZE = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private final Path productUploadDir = Path.of("src", "main", "resources", "static", "uploads", "products");

    @Override
    public UploadedFile storeProductImage(MultipartFile file) {
        validate(file);
        String extension = extension(file.getOriginalFilename());
        String publicId = UUID.randomUUID() + "." + extension;
        Path target = productUploadDir.resolve(publicId).normalize();
        try {
            Files.createDirectories(productUploadDir);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new BusinessException("Could not store product image");
        }
        return new UploadedFile("/uploads/products/" + publicId, publicId);
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(productUploadDir.resolve(publicId).normalize());
        } catch (IOException ex) {
            throw new BusinessException("Could not delete product image file");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Image size must be 5MB or less");
        }
        String extension = extension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("Only JPG, JPEG, PNG, and WEBP images are allowed");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException("Uploaded file must be an image");
        }
    }

    private String extension(String filename) {
        String cleaned = StringUtils.cleanPath(filename == null ? "" : filename);
        int dotIndex = cleaned.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == cleaned.length() - 1) {
            throw new BusinessException("Image file extension is required");
        }
        return cleaned.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
