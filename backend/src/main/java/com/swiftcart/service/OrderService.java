package com.swiftcart.service;

import com.swiftcart.dto.OrderDtos.OrderResponse;
import com.swiftcart.dto.OrderDtos.OrderTrackingTimelineResponse;
import com.swiftcart.dto.OrderDtos.PlaceOrderRequest;
import com.swiftcart.dto.OrderDtos.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse place(String email, PlaceOrderRequest request);
    Page<OrderResponse> history(String email, Pageable pageable);
    Page<OrderResponse> manage(Pageable pageable);
    OrderTrackingTimelineResponse tracking(String email, String orderNumber);
    OrderTrackingTimelineResponse updateStatus(String email, String orderNumber, UpdateOrderStatusRequest request);
}
