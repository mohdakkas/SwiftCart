package com.swiftcart.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    UploadedFile storeProductImage(MultipartFile file);

    void delete(String publicId);

    record UploadedFile(String url, String publicId) {
    }
}
