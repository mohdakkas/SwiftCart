package com.swiftcart.service.impl;

import com.swiftcart.dto.AdminAnalyticsDtos.DashboardSummaryResponse;
import com.swiftcart.dto.AdminAnalyticsDtos.RecentOrderResponse;
import com.swiftcart.dto.AdminAnalyticsDtos.TopProductResponse;
import com.swiftcart.entity.Order;
import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.repository.OrderItemRepository;
import com.swiftcart.repository.OrderRepository;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.AdminAnalyticsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public AdminAnalyticsServiceImpl(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Map<String, Object> dashboard() {
        DashboardSummaryResponse summary = summary();
        return Map.of(
                "summary", summary,
                "totalUsers", summary.totalUsers(),
                "totalProducts", summary.totalProducts(),
                "totalOrders", summary.totalOrders(),
                "totalRevenue", summary.totalRevenue(),
                "topSellingProducts", topProducts(),
                "recentOrders", recentOrders()
        );
    }

    @Override
    public DashboardSummaryResponse summary() {
        BigDecimal revenue = orderRepository.totalSalesAmount();
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        return new DashboardSummaryResponse(
                orderItemRepository.totalUnitsSold(),
                revenue == null ? BigDecimal.ZERO : revenue,
                orderRepository.count(),
                userRepository.count(),
                productRepository.count(),
                orderRepository.countByStatus(OrderStatus.PLACED),
                deliveredOrders,
                orderRepository.countByStatus(OrderStatus.CANCELLED)
        );
    }

    @Override
    public List<TopProductResponse> topProducts() {
        return productRepository.findTopSellingProducts(PageRequest.of(0, 10)).stream()
                .map(this::toTopProduct)
                .toList();
    }

    private TopProductResponse toTopProduct(Object[] row) {
        Long productId = (Long) row[0];
        String imageUrl = productRepository.findById(productId)
                .flatMap(product -> product.getImages().stream().findFirst().map(image -> image.getImageUrl()))
                .orElse("");
        return new TopProductResponse(
                productId,
                (String) row[1],
                imageUrl,
                ((Number) row[2]).longValue(),
                (BigDecimal) row[3]
        );
    }

    @Override
    public List<RecentOrderResponse> recentOrders() {
        return orderRepository.findRecentOrders(PageRequest.of(0, 10)).stream()
                .map(this::toRecentOrder)
                .toList();
    }

    private RecentOrderResponse toRecentOrder(Order order) {
        String customerName = order.getUser().getFirstName() + " " + order.getUser().getLastName();
        PaymentStatus paymentStatus = order.getPayment() == null ? PaymentStatus.PENDING : order.getPayment().getStatus();
        return new RecentOrderResponse(
                order.getOrderNumber(),
                customerName,
                order.getTotalAmount(),
                order.getStatus(),
                paymentStatus,
                order.getCreatedAt()
        );
    }
}
