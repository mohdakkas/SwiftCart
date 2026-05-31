package com.swiftcart.service;

import com.swiftcart.dto.CartDtos.AddCartItemRequest;
import com.swiftcart.dto.CartDtos.CartResponse;
import com.swiftcart.dto.CartDtos.UpdateCartItemRequest;

public interface CartService {
    CartResponse current(String email);
    CartResponse add(String email, AddCartItemRequest request);
    CartResponse update(String email, Long itemId, UpdateCartItemRequest request);
    CartResponse remove(String email, Long itemId);
}
