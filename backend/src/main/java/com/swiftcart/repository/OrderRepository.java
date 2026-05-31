package com.swiftcart.repository;

import com.swiftcart.entity.Order;
import com.swiftcart.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
    long countByStatus(OrderStatus status);

    @Query("""
        select coalesce(sum(o.totalAmount), 0)
        from Order o
        where o.status not in (
            com.swiftcart.enums.OrderStatus.CANCELLED,
            com.swiftcart.enums.OrderStatus.RETURNED,
            com.swiftcart.enums.OrderStatus.REFUNDED
        )
        """)
    BigDecimal totalSalesAmount();

    @Query("select o from Order o left join fetch o.user left join fetch o.payment order by o.createdAt desc")
    List<Order> findRecentOrders(Pageable pageable);
}
