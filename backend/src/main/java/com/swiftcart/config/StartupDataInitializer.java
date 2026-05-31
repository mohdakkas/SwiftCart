package com.swiftcart.config;

import com.swiftcart.entity.Cart;
import com.swiftcart.entity.Category;
import com.swiftcart.entity.Product;
import com.swiftcart.entity.ProductImage;
import com.swiftcart.entity.Role;
import com.swiftcart.entity.User;
import com.swiftcart.entity.Wishlist;
import com.swiftcart.repository.CategoryRepository;
import com.swiftcart.repository.ProductRepository;
import com.swiftcart.repository.RoleRepository;
import com.swiftcart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Configuration
public class StartupDataInitializer {
    @Bean
    CommandLineRunner seedSwiftCartData(RoleRepository roleRepository,
                                        UserRepository userRepository,
                                        CategoryRepository categoryRepository,
                                        ProductRepository productRepository,
                                        PasswordEncoder passwordEncoder,
                                        PlatformTransactionManager transactionManager) {
        return args -> new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                seed(roleRepository, userRepository, categoryRepository, productRepository, passwordEncoder)
        );
    }

    void seed(RoleRepository roleRepository,
              UserRepository userRepository,
              CategoryRepository categoryRepository,
              ProductRepository productRepository,
              PasswordEncoder passwordEncoder) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_USER");
            return roleRepository.save(role);
        });

        User admin = userRepository.findByEmail("admin@swiftcart.com").orElseGet(User::new);
        admin.setEmail("admin@swiftcart.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setFirstName("SwiftCart");
        admin.setLastName("Admin");
        admin.setPhone("9999999999");
        admin.setEmailVerified(true);
        admin.getRoles().add(adminRole);
        admin.getRoles().add(userRole);
        attachShoppingContainers(admin);
        userRepository.save(admin);

        User user = userRepository.findByEmail("maya@swiftcart.com").orElseGet(User::new);
        user.setEmail("maya@swiftcart.com");
        user.setPassword(passwordEncoder.encode("User@12345"));
        user.setFirstName("Maya");
        user.setLastName("Rao");
        user.setPhone("8888888888");
        user.setEmailVerified(true);
        user.getRoles().add(userRole);
        attachShoppingContainers(user);
        userRepository.save(user);

        Category electronicsDraft = categoryRepository.findBySlug("electronics").orElseGet(() -> category("Electronics", "electronics", "https://images.unsplash.com/photo-1498049794561-7780e7231661", null));
        Category electronics = categoryRepository.save(electronicsDraft);
        Category fashionDraft = categoryRepository.findBySlug("fashion").orElseGet(() -> category("Fashion", "fashion", "https://images.unsplash.com/photo-1445205170230-053b83016050", null));
        Category fashion = categoryRepository.save(fashionDraft);
        Category mobiles = categoryRepository.findBySlug("mobiles").orElseGet(() -> category("Mobiles", "mobiles", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9", electronics));
        mobiles.setParent(electronics);
        mobiles = categoryRepository.save(mobiles);
        Category sneakers = categoryRepository.findBySlug("sneakers").orElseGet(() -> category("Sneakers", "sneakers", "https://images.unsplash.com/photo-1542291026-7eec264c27ff", fashion));
        sneakers.setParent(fashion);
        sneakers = categoryRepository.save(sneakers);

        seedProduct(productRepository, mobiles, "SC-PHONE-X1", "SwiftPhone X1", "swiftphone-x1", "Flagship phone with OLED display, fast charging, and pro-grade camera.", "69999.00", "62999.00", 75, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9");
        seedProduct(productRepository, electronics, "SC-BUDS-PRO", "SwiftBuds Pro", "swiftbuds-pro", "Noise cancelling wireless earbuds with spatial audio.", "14999.00", "11999.00", 140, "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1");
        seedProduct(productRepository, sneakers, "SC-RUN-AIR", "RunAir Knit Sneakers", "runair-knit-sneakers", "Lightweight everyday sneakers with breathable knit upper.", "6499.00", "4999.00", 210, "https://images.unsplash.com/photo-1542291026-7eec264c27ff");
    }

    private void attachShoppingContainers(User user) {
        if (user.getCart() == null) {
            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }
        if (user.getWishlist() == null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            user.setWishlist(wishlist);
        }
    }

    private Category category(String name, String slug, String imageUrl, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setImageUrl(imageUrl);
        category.setParent(parent);
        category.setActive(true);
        return category;
    }

    private void seedProduct(ProductRepository productRepository, Category category, String sku, String name, String slug,
                             String description, String price, String salePrice, int stockQuantity, String imageUrl) {
        Product product = productRepository.findBySlug(slug).orElseGet(Product::new);
        product.setCategory(category);
        product.setSku(sku);
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setSalePrice(new BigDecimal(salePrice));
        product.setStockQuantity(stockQuantity);
        product.setAverageRating(new BigDecimal("4.6"));
        product.setReviewCount(80);
        product.setFeatured(true);
        product.setTrending(true);
        product.setBestSeller("SC-PHONE-X1".equals(sku));
        product.setNewArrival("SC-RUN-AIR".equals(sku));
        product.setBadge(product.getStockQuantity() < 100 ? "LIMITED STOCK" : "SALE");
        product.setActive(true);
        if (product.getImages().isEmpty()) {
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(imageUrl);
            image.setAltText(name);
            image.setPrimaryImage(true);
            image.setSortOrder(1);
            product.getImages().add(image);
        }
        productRepository.save(product);
    }
}
