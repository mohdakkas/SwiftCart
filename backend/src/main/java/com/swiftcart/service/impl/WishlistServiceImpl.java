package com.swiftcart.service.impl;

import com.swiftcart.dto.CartDtos.AddCartItemRequest;
import com.swiftcart.dto.CartDtos.CartResponse;
import com.swiftcart.dto.WishlistDtos.AddWishlistItemRequest;
import com.swiftcart.dto.WishlistDtos.WishlistItemResponse;
import com.swiftcart.dto.WishlistDtos.WishlistResponse;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.User;
import com.swiftcart.entity.Wishlist;
import com.swiftcart.entity.WishlistItem;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.repository.WishlistRepository;
import com.swiftcart.service.CartService;
import com.swiftcart.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, ProductRepository productRepository, UserRepository userRepository, CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse current(String email) {
        return toResponse(wishlist(email));
    }

    @Override
    public WishlistResponse add(String email, AddWishlistItemRequest request) {
        Wishlist wishlist = wishlist(email);
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        boolean exists = wishlist.getItems().stream().anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        if (exists) {
            throw new BusinessException("Product already exists in wishlist");
        }
        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProduct(product);
        wishlist.getItems().add(item);
        return toResponse(wishlist);
    }

    @Override
    public WishlistResponse remove(String email, Long itemId) {
        Wishlist wishlist = wishlist(email);
        wishlist.getItems().removeIf(item -> item.getId().equals(itemId));
        return toResponse(wishlist);
    }

    @Override
    public CartResponse moveToCart(String email, Long itemId) {
        Wishlist wishlist = wishlist(email);
        WishlistItem item = wishlist.getItems().stream()
                .filter(candidate -> candidate.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        Long productId = item.getProduct().getId();
        wishlist.getItems().remove(item);
        return cartService.add(email, new AddCartItemRequest(productId, 1));
    }

    private Wishlist wishlist(String email) {
        return wishlistRepository.findByUserEmail(email).orElseGet(() -> {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            return wishlistRepository.save(wishlist);
        });
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        return new WishlistResponse(wishlist.getItems().stream().map(item -> {
            Product product = item.getProduct();
            String imageUrl = product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl();
            return new WishlistItemResponse(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getSalePrice() == null ? product.getPrice() : product.getSalePrice(),
                    imageUrl
            );
        }).toList());
    }
}
