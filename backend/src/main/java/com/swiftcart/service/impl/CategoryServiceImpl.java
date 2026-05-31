package com.swiftcart.service.impl;

import com.swiftcart.dto.CategoryDtos.CategoryRequest;
import com.swiftcart.dto.CategoryDtos.CategoryResponse;
import com.swiftcart.entity.Category;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.CategoryRepository;
import com.swiftcart.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> roots() {
        return categoryRepository.findByParentIsNullAndActiveTrueOrderByNameAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        apply(category, request);
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        apply(category, request);
        return toResponse(category);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setActive(false);
    }

    private void apply(Category category, CategoryRequest request) {
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setImageUrl(request.imageUrl());
        category.setActive(request.active() == null || request.active());
        category.setParent(request.parentId() == null ? null : categoryRepository.findById(request.parentId()).orElseThrow(() -> new ResourceNotFoundException("Parent category not found")));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug(), category.getImageUrl(), category.getParent() == null ? null : category.getParent().getId(), category.isActive());
    }
}
