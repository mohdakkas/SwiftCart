package com.swiftcart.dto;

import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public final class AdminAnalyticsDtos {
    private AdminAnalyticsDtos() {
    }

    public record DashboardSummaryResponse(
            long totalSales,
            BigDecimal totalRevenue,
            long totalOrders,
            long totalUsers,
            long totalProducts,
            long pendingOrders,
            long deliveredOrders,
            long cancelledOrders
    ) {
    }

    public record TopProductResponse(
            Long productId,
            String productName,
            String imageUrl,
            long totalSold,
            BigDecimal totalRevenue
    ) {
    }

    public record RecentOrderResponse(
            String orderNumber,
            String customerName,
            BigDecimal totalAmount,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            Instant createdAt
    ) {
    }
}
