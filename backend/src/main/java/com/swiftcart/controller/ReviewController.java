package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.ReviewDtos.ReviewRequest;
import com.swiftcart.dto.ReviewDtos.ReviewResponse;
import com.swiftcart.dto.ReviewDtos.ReviewUpdateRequest;
import com.swiftcart.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(Authentication authentication, @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Review submitted", reviewService.create(authentication.getName(), request)));
    }

    @PutMapping("/reviews/{id}")
    public ApiResponse<ReviewResponse> update(Authentication authentication, @PathVariable Long id, @Valid @RequestBody ReviewUpdateRequest request) {
        return ApiResponse.ok("Review updated", reviewService.update(authentication.getName(), id, request));
    }

    @GetMapping("/products/{id}/reviews")
    public ApiResponse<List<ReviewResponse>> productReviews(@PathVariable Long id) {
        return ApiResponse.ok("Reviews loaded", reviewService.productReviews(id));
    }

    @DeleteMapping("/reviews/{id}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
        reviewService.delete(authentication.getName(), id);
        return ApiResponse.ok("Review deleted", null);
    }
}
