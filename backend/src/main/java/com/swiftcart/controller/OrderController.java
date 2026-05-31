package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.OrderDtos.OrderResponse;
import com.swiftcart.dto.OrderDtos.OrderTrackingTimelineResponse;
import com.swiftcart.dto.OrderDtos.PlaceOrderRequest;
import com.swiftcart.dto.OrderDtos.UpdateOrderStatusRequest;
import com.swiftcart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> place(Authentication authentication, @Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.ok("Order placed", orderService.place(authentication.getName(), request));
    }

    @GetMapping
    public ApiResponse<Page<OrderResponse>> history(Authentication authentication, Pageable pageable) {
        return ApiResponse.ok("Orders loaded", orderService.history(authentication.getName(), pageable));
    }

    @GetMapping("/{orderNumber}/tracking")
    public ApiResponse<OrderTrackingTimelineResponse> tracking(Authentication authentication, @PathVariable String orderNumber) {
        return ApiResponse.ok("Order tracking loaded", orderService.tracking(authentication.getName(), orderNumber));
    }

    @GetMapping("/manage")
    public ApiResponse<Page<OrderResponse>> manage(Pageable pageable) {
        return ApiResponse.ok("Orders loaded", orderService.manage(pageable));
    }

    @GetMapping("/manage/{orderNumber}")
    public ApiResponse<String> manage(@PathVariable String orderNumber) {
        return ApiResponse.ok("Order management access granted", orderNumber);
    }

    @PatchMapping("/manage/{orderNumber}/status")
    public ApiResponse<OrderTrackingTimelineResponse> updateStatus(Authentication authentication,
                                                                  @PathVariable String orderNumber,
                                                                  @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.ok("Order status updated", orderService.updateStatus(authentication.getName(), orderNumber, request));
    }
}
