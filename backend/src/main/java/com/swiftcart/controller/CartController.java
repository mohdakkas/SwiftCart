package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.CartDtos.AddCartItemRequest;
import com.swiftcart.dto.CartDtos.CartResponse;
import com.swiftcart.dto.CartDtos.UpdateCartItemRequest;
import com.swiftcart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartResponse> current(Authentication authentication) {
        return ApiResponse.ok("Cart loaded", cartService.current(authentication.getName()));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> add(Authentication authentication, @Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.ok("Item added", cartService.add(authentication.getName(), request));
    }

    @PatchMapping("/items/{itemId}")
    public ApiResponse<CartResponse> update(Authentication authentication, @PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.ok("Item updated", cartService.update(authentication.getName(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> remove(Authentication authentication, @PathVariable Long itemId) {
        return ApiResponse.ok("Item removed", cartService.remove(authentication.getName(), itemId));
    }
}
