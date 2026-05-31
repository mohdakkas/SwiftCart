package com.swiftcart.service;

import com.swiftcart.dto.ProductDtos.ProductRequest;
import com.swiftcart.dto.ProductDtos.ProductResponse;
import com.swiftcart.dto.ProductDtos.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> search(String query, Long categoryId, Pageable pageable);
    ProductResponse getById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductUpdateRequest request);
    void delete(Long id);
}
