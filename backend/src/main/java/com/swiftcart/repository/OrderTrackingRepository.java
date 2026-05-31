package com.swiftcart.repository;

import com.swiftcart.entity.OrderTracking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    @EntityGraph(attributePaths = {"order", "order.user"})
    List<OrderTracking> findByOrderOrderNumberOrderByCreatedAtAsc(String orderNumber);
}
