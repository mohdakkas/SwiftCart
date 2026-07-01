package com.swiftcart.service.impl;

import com.swiftcart.dto.ProductDtos.ProductRequest;
import com.swiftcart.dto.ProductDtos.ProductResponse;
import com.swiftcart.dto.ProductDtos.ProductUpdateRequest;
import com.swiftcart.entity.Category;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.ProductImage;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.mapper.ProductMapper;
import com.swiftcart.repository.CategoryRepository;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Long categoryId, Pageable pageable) {
        return productRepository.search(query, categoryId, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productRepository.findById(id).map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = new Product();
        product.setCategory(category);
        product.setSku(request.sku());
        product.setName(request.name());
        product.setSlug(request.slug());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSalePrice(request.salePrice());
        product.setStockQuantity(request.stockQuantity());
        product.setFeatured(Boolean.TRUE.equals(request.featured()));
        product.setTrending(Boolean.TRUE.equals(request.trending()));
        product.setBestSeller(Boolean.TRUE.equals(request.bestSeller()));
        product.setNewArrival(Boolean.TRUE.equals(request.newArrival()));
        product.setBadge(request.badge());
        if (request.images() != null) {
            for (int i = 0; i < request.images().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(request.images().get(i));
                image.setPrimaryImage(i == 0);
                image.setSortOrder(i + 1);
                product.getImages().add(image);
            }
        }
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (request.categoryId() != null) {
            product.setCategory(categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }
        if (request.sku() != null) product.setSku(request.sku());
        if (request.name() != null) product.setName(request.name());
        if (request.slug() != null) product.setSlug(request.slug());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null) product.setPrice(request.price());
        if (request.salePrice() != null) product.setSalePrice(request.salePrice());
        if (request.stockQuantity() != null) product.setStockQuantity(request.stockQuantity());
        if (request.featured() != null) product.setFeatured(request.featured());
        if (request.trending() != null) product.setTrending(request.trending());
        if (request.bestSeller() != null) product.setBestSeller(request.bestSeller());
        if (request.newArrival() != null) product.setNewArrival(request.newArrival());
        if (request.badge() != null) product.setBadge(request.badge());
        if (request.active() != null) product.setActive(request.active());
        return productMapper.toResponse(product);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false);
    }
}

