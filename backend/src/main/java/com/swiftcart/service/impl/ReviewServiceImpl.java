package com.swiftcart.service.impl;

import com.swiftcart.dto.ReviewDtos.ReviewRequest;
import com.swiftcart.dto.ReviewDtos.ReviewResponse;
import com.swiftcart.dto.ReviewDtos.ReviewUpdateRequest;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.Review;
import com.swiftcart.entity.User;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.repository.ReviewRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.ReviewService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponse create(String email, ReviewRequest request) {
        String normalizedEmail = email.toLowerCase();
        if (reviewRepository.existsByProduct_IdAndUser_Email(request.productId(), normalizedEmail)) {
            throw new BusinessException("You have already reviewed this product");
        }
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setTitle(request.title().trim());
        review.setComment(request.comment().trim());

        Review saved = reviewRepository.save(review);
        recalculateProductRating(product.getId());
        return toResponse(saved);
    }

    @Override
    public ReviewResponse update(String email, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (!review.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("You can edit only your own review");
        }
        review.setRating(request.rating());
        review.setTitle(request.title().trim());
        review.setComment(request.comment().trim());
        recalculateProductRating(review.getProduct().getId());
        return toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> productReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return reviewRepository.findByProduct_IdOrderByCreatedAtDesc(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(String email, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        boolean admin = userRepository.findByEmail(email.toLowerCase())
                .map(user -> user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName())))
                .orElse(false);
        boolean owner = review.getUser().getEmail().equalsIgnoreCase(email);
        if (!admin && !owner) {
            throw new AccessDeniedException("You can delete only your own review");
        }
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        recalculateProductRating(productId);
    }

    private void recalculateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        long reviewCount = reviewRepository.countByProduct_Id(productId);
        double average = reviewRepository.averageRatingByProductId(productId);
        product.setReviewCount((int) reviewCount);
        product.setAverageRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
    }

    private ReviewResponse toResponse(Review review) {
        User user = review.getUser();
        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getTitle(),
                review.getComment(),
                review.getCreatedAt(),
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                review.getProduct().getId()
        );
    }
}
