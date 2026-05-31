package com.swiftcart.repository;

import com.swiftcart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);

    @Query("""
        select p from Product p
        where p.active = true
          and (:query is null or lower(p.name) like lower(concat('%', :query, '%')) or lower(p.description) like lower(concat('%', :query, '%')))
          and (:categoryId is null or p.category.id = :categoryId)
        """)
    Page<Product> search(@Param("query") String query, @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
        select p.id, p.name, coalesce(sum(oi.quantity), 0), coalesce(sum(oi.totalPrice), 0)
        from OrderItem oi
        join oi.product p
        group by p.id, p.name
        order by coalesce(sum(oi.quantity), 0) desc, coalesce(sum(oi.totalPrice), 0) desc
        """)
    List<Object[]> findTopSellingProducts(Pageable pageable);
}
