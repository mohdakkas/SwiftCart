package com.swiftcart.controller;

import com.swiftcart.dto.AdminAnalyticsDtos.DashboardSummaryResponse;
import com.swiftcart.dto.AdminAnalyticsDtos.RecentOrderResponse;
import com.swiftcart.dto.AdminAnalyticsDtos.TopProductResponse;
import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.ProductDtos.ProductRequest;
import com.swiftcart.dto.ProductDtos.ProductResponse;
import com.swiftcart.service.AdminAnalyticsService;
import com.swiftcart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminAnalyticsService adminAnalyticsService;
    private final ProductService productService;

    public AdminController(AdminAnalyticsService adminAnalyticsService, ProductService productService) {
        this.adminAnalyticsService = adminAnalyticsService;
        this.productService = productService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok("Dashboard analytics loaded", adminAnalyticsService.dashboard());
    }

    @GetMapping("/dashboard/summary")
    public ApiResponse<DashboardSummaryResponse> summary() {
        return ApiResponse.ok("Dashboard summary loaded", adminAnalyticsService.summary());
    }

    @GetMapping("/dashboard/top-products")
    public ApiResponse<List<TopProductResponse>> topProducts() {
        return ApiResponse.ok("Top-selling products loaded", adminAnalyticsService.topProducts());
    }

    @GetMapping("/dashboard/recent-orders")
    public ApiResponse<List<RecentOrderResponse>> recentOrders() {
        return ApiResponse.ok("Recent orders loaded", adminAnalyticsService.recentOrders());
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Product created", productService.create(request)));
    }
}
