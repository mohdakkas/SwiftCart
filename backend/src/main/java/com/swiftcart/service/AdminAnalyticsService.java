package com.swiftcart.service;

import com.swiftcart.dto.AdminAnalyticsDtos.DashboardSummaryResponse;
import com.swiftcart.dto.AdminAnalyticsDtos.RecentOrderResponse;
import com.swiftcart.dto.AdminAnalyticsDtos.TopProductResponse;

import java.util.List;
import java.util.Map;

public interface AdminAnalyticsService {
    Map<String, Object> dashboard();
    DashboardSummaryResponse summary();
    List<TopProductResponse> topProducts();
    List<RecentOrderResponse> recentOrders();
}
