package com.swiftcart.service;

import com.swiftcart.dto.CategoryDtos.CategoryRequest;
import com.swiftcart.dto.CategoryDtos.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> roots();
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}
