package com.swiftcart.service;

import com.swiftcart.dto.ReviewDtos.ReviewRequest;
import com.swiftcart.dto.ReviewDtos.ReviewResponse;
import com.swiftcart.dto.ReviewDtos.ReviewUpdateRequest;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(String email, ReviewRequest request);

    ReviewResponse update(String email, Long reviewId, ReviewUpdateRequest request);

    List<ReviewResponse> productReviews(Long productId);

    void delete(String email, Long reviewId);
}
