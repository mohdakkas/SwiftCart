package com.swiftcart.repository;

import com.swiftcart.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"user", "product"})
    List<Review> findByProduct_IdOrderByCreatedAtDesc(Long productId);

    Optional<Review> findByProduct_IdAndUser_Email(Long productId, String email);

    boolean existsByProduct_IdAndUser_Email(Long productId, String email);

    long countByProduct_Id(Long productId);

    @Query("select coalesce(avg(r.rating), 0) from Review r where r.product.id = :productId")
    double averageRatingByProductId(@Param("productId") Long productId);
}
