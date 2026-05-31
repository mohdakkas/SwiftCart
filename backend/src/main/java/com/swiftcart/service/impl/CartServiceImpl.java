package com.swiftcart.service.impl;

import com.swiftcart.dto.CartDtos.AddCartItemRequest;
import com.swiftcart.dto.CartDtos.CartItemResponse;
import com.swiftcart.dto.CartDtos.CartResponse;
import com.swiftcart.dto.CartDtos.UpdateCartItemRequest;
import com.swiftcart.entity.Cart;
import com.swiftcart.entity.CartItem;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.User;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.CartItemRepository;
import com.swiftcart.repository.CartRepository;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse current(String email) {
        return toResponse(cart(email));
    }

    @Override
    public CartResponse add(String email, AddCartItemRequest request) {
        Cart cart = cart(email);
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStockQuantity() < request.quantity()) {
            throw new BusinessException("Requested quantity is not available");
        }
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElseGet(CartItem::new);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(item.getQuantity() + request.quantity());
        item.setUnitPrice(product.getSalePrice() != null ? product.getSalePrice() : product.getPrice());
        cartItemRepository.save(item);
        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    public CartResponse update(String email, Long itemId, UpdateCartItemRequest request) {
        Cart cart = cart(email);
        CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Cart item does not belong to current user");
        }
        item.setQuantity(request.quantity());
        return toResponse(cart);
    }

    @Override
    public CartResponse remove(String email, Long itemId) {
        Cart cart = cart(email);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartItemRepository.deleteById(itemId);
        return toResponse(cart);
    }

    private Cart cart(String email) {
        return cartRepository.findByUserEmail(email).orElseGet(() -> {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private CartResponse toResponse(Cart cart) {
        var items = cart.getItems().stream()
                .map(item -> new CartItemResponse(item.getId(), item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getUnitPrice(), item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .toList();
        BigDecimal subtotal = items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal shipping = subtotal.compareTo(new BigDecimal("999")) > 0 ? BigDecimal.ZERO : new BigDecimal("99");
        return new CartResponse(items, subtotal, tax, shipping, subtotal.add(tax).add(shipping));
    }
}
