package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.CartDtos.CartResponse;
import com.swiftcart.dto.WishlistDtos.AddWishlistItemRequest;
import com.swiftcart.dto.WishlistDtos.WishlistResponse;
import com.swiftcart.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ApiResponse<WishlistResponse> current(Authentication authentication) {
        return ApiResponse.ok("Wishlist loaded", wishlistService.current(authentication.getName()));
    }

    @PostMapping("/items")
    public ApiResponse<WishlistResponse> add(Authentication authentication, @Valid @RequestBody AddWishlistItemRequest request) {
        return ApiResponse.ok("Wishlist item added", wishlistService.add(authentication.getName(), request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<WishlistResponse> remove(Authentication authentication, @PathVariable Long itemId) {
        return ApiResponse.ok("Wishlist item removed", wishlistService.remove(authentication.getName(), itemId));
    }

    @PostMapping("/items/{itemId}/move-to-cart")
    public ApiResponse<CartResponse> moveToCart(Authentication authentication, @PathVariable Long itemId) {
        return ApiResponse.ok("Wishlist item moved to cart", wishlistService.moveToCart(authentication.getName(), itemId));
    }
}
