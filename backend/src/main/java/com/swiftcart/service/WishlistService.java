package com.swiftcart.service;

import com.swiftcart.dto.CartDtos.CartResponse;
import com.swiftcart.dto.WishlistDtos.AddWishlistItemRequest;
import com.swiftcart.dto.WishlistDtos.WishlistResponse;

public interface WishlistService {
    WishlistResponse current(String email);
    WishlistResponse add(String email, AddWishlistItemRequest request);
    WishlistResponse remove(String email, Long itemId);
    CartResponse moveToCart(String email, Long itemId);
}
