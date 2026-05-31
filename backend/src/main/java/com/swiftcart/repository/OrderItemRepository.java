package com.swiftcart.repository;

import com.swiftcart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("select coalesce(sum(oi.quantity), 0) from OrderItem oi")
    long totalUnitsSold();
}
