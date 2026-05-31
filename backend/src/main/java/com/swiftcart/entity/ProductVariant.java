package com.swiftcart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String color;
    private String size;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal priceDelta = BigDecimal.ZERO;

    private int stockQuantity;
}
