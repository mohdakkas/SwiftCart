package com.swiftcart.config;

import com.swiftcart.entity.Address;
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
import java.util.LinkedHashMap;
import java.util.Map;

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
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(role("ROLE_ADMIN")));
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(role("ROLE_USER")));

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
        attachDefaultAddress(admin);
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
        attachDefaultAddress(user);
        userRepository.save(user);

        Map<String, Category> categories = new LinkedHashMap<>();
        categories.put("electronics", saveCategory(categoryRepository, "Electronics", "electronics", "https://images.unsplash.com/photo-1498049794561-7780e7231661"));
        categories.put("mobiles", saveCategory(categoryRepository, "Mobiles", "mobiles", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        categories.put("laptops", saveCategory(categoryRepository, "Laptops", "laptops", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"));
        categories.put("fashion", saveCategory(categoryRepository, "Fashion", "fashion", "https://images.unsplash.com/photo-1445205170230-053b83016050"));
        categories.put("shoes", saveCategory(categoryRepository, "Shoes", "shoes", "https://images.unsplash.com/photo-1542291026-7eec264c27ff"));
        categories.put("watches", saveCategory(categoryRepository, "Watches", "watches", "https://images.unsplash.com/photo-1523275335684-37898b6baf30"));
        categories.put("home-appliances", saveCategory(categoryRepository, "Home Appliances", "home-appliances", "https://images.unsplash.com/photo-1556911220-bff31c812dba"));
        categories.put("kitchen", saveCategory(categoryRepository, "Kitchen Products", "kitchen", "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136"));
        categories.put("books", saveCategory(categoryRepository, "Books", "books", "https://images.unsplash.com/photo-1512820790803-83ca734da794"));
        categories.put("gaming", saveCategory(categoryRepository, "Gaming Accessories", "gaming", "https://images.unsplash.com/photo-1593305841991-05c297ba4575"));

        Object[][] products = new Object[][]{
                {"mobiles","SC-MOB-001","iPhone 15 Pro 256GB","Apple","iphone-15-pro-256gb","Titanium 5G smartphone with pro camera system and all-day battery.",134900,124900,42,4.8,"https://images.unsplash.com/photo-1695048133142-1a20484d2569"},
                {"mobiles","SC-MOB-002","Samsung Galaxy S24 Ultra","Samsung","samsung-galaxy-s24-ultra","Flagship Android phone with S Pen, AI camera, and brilliant AMOLED display.",129999,114999,58,4.7,"https://images.unsplash.com/photo-1610945265064-0e34e5519bbf"},
                {"mobiles","SC-MOB-003","OnePlus 12 5G","OnePlus","oneplus-12-5g","Fast flagship phone with Hasselblad camera and rapid charging.",64999,59999,76,4.6,"https://images.unsplash.com/photo-1598327105666-5b89351aff97"},
                {"mobiles","SC-MOB-004","Redmi Note 13 Pro+","Xiaomi","redmi-note-13-pro-plus","Curved AMOLED display, 200MP camera, and premium fast charging.",33999,29999,110,4.4,"https://images.unsplash.com/photo-1592899677977-9c10ca588bbd"},
                {"electronics","SC-ELC-001","Sony WH-1000XM5 Headphones","Sony","sony-wh-1000xm5-headphones","Industry-leading noise cancelling wireless headphones for travel and work.",34990,28990,64,4.8,"https://images.unsplash.com/photo-1505740420928-5e560c06d30e"},
                {"electronics","SC-ELC-002","JBL Flip 6 Bluetooth Speaker","JBL","jbl-flip-6-speaker","Portable waterproof speaker with punchy sound and long battery life.",11999,8999,95,4.5,"https://images.unsplash.com/photo-1608043152269-423dbba4e7e1"},
                {"electronics","SC-ELC-003","Anker PowerCore 20000mAh","Anker","anker-powercore-20000","High-capacity power bank with fast charging for phones and tablets.",4999,3499,130,4.4,"https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5"},
                {"electronics","SC-ELC-004","Samsung Galaxy Buds2 Pro","Samsung","galaxy-buds2-pro","Compact ANC earbuds with immersive audio and clear calls.",17999,12999,88,4.5,"https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1"},
                {"laptops","SC-LAP-001","MacBook Air M3 13-inch","Apple","macbook-air-m3-13","Ultra-thin laptop with M3 performance, Retina display, and silent design.",114900,104900,28,4.9,"https://images.unsplash.com/photo-1517336714731-489689fd1ca8"},
                {"laptops","SC-LAP-002","Dell XPS 13 Plus","Dell","dell-xps-13-plus","Premium ultrabook with edge-to-edge display and productivity performance.",139990,124990,18,4.6,"https://images.unsplash.com/photo-1496181133206-80ce9b88a853"},
                {"laptops","SC-LAP-003","HP Pavilion 15 Ryzen 7","HP","hp-pavilion-15-ryzen-7","Everyday performance laptop for work, study, and entertainment.",74999,64999,44,4.3,"https://images.unsplash.com/photo-1484788984921-03950022c9ef"},
                {"laptops","SC-LAP-004","Lenovo Legion 5 Gaming Laptop","Lenovo","lenovo-legion-5-gaming","Gaming laptop with RTX graphics, high-refresh display, and thermal cooling.",119999,104999,25,4.6,"https://images.unsplash.com/photo-1603302576837-37561b2e2302"},
                {"fashion","SC-FAS-001","Oxford Slim Fit Shirt","Van Heusen","oxford-slim-fit-shirt","Premium cotton formal shirt with tailored fit for office wear.",2499,1499,160,4.2,"https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf"},
                {"fashion","SC-FAS-002","Women Floral Midi Dress","Zara","women-floral-midi-dress","Elegant floral midi dress with breathable fabric and soft drape.",3999,2499,90,4.4,"https://images.unsplash.com/photo-1515372039744-b8f02a3ae446"},
                {"fashion","SC-FAS-003","Men Graphic T-Shirt","H&M","men-graphic-tshirt","Comfort fit cotton tee with durable print and everyday styling.",1299,799,220,4.1,"https://images.unsplash.com/photo-1521572163474-6864f9cf17ab"},
                {"fashion","SC-FAS-004","Classic Denim Jacket","Levi's","classic-denim-jacket","Timeless denim jacket with rugged stitching and versatile layering.",4999,3499,70,4.5,"https://images.unsplash.com/photo-1543076447-215ad9ba6923"},
                {"shoes","SC-SHO-001","Nike Air Max Runner","Nike","nike-air-max-runner","Cushioned running shoes with breathable mesh and responsive sole.",9995,7995,86,4.6,"https://images.unsplash.com/photo-1542291026-7eec264c27ff"},
                {"shoes","SC-SHO-002","Adidas Ultraboost Light","Adidas","adidas-ultraboost-light","Lightweight performance running shoes with energy return foam.",17999,13999,52,4.7,"https://images.unsplash.com/photo-1608231387042-66d1773070a5"},
                {"shoes","SC-SHO-003","Puma Court Sneakers","Puma","puma-court-sneakers","Minimal low-top sneakers for daily streetwear styling.",5999,3999,120,4.3,"https://images.unsplash.com/photo-1600185365483-26d7a4cc7519"},
                {"shoes","SC-SHO-004","Skechers Go Walk","Skechers","skechers-go-walk","Comfort walking shoes with lightweight cushioning and slip-on fit.",6499,4999,100,4.4,"https://images.unsplash.com/photo-1549298916-b41d501d3772"},
                {"watches","SC-WAT-001","Apple Watch Series 9","Apple","apple-watch-series-9","Advanced smartwatch with health sensors, GPS, and bright always-on display.",41900,38900,40,4.8,"https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d"},
                {"watches","SC-WAT-002","Samsung Galaxy Watch6","Samsung","samsung-galaxy-watch6","Fitness smartwatch with body insights, sleep tracking, and AMOLED display.",29999,23999,65,4.5,"https://images.unsplash.com/photo-1523275335684-37898b6baf30"},
                {"watches","SC-WAT-003","Fossil Gen 6 Leather","Fossil","fossil-gen-6-leather","Classic smartwatch with leather strap and Wear OS features.",24995,16995,45,4.2,"https://images.unsplash.com/photo-1524592094714-0f0654e20314"},
                {"home-appliances","SC-HOM-001","LG 260L Double Door Refrigerator","LG","lg-260l-double-door-fridge","Energy-efficient refrigerator with smart inverter cooling and spacious storage.",38990,32990,22,4.4,"https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5"},
                {"home-appliances","SC-HOM-002","Samsung 7kg Front Load Washer","Samsung","samsung-7kg-front-load-washer","Quiet front-load washing machine with hygiene steam and digital inverter motor.",42990,36990,18,4.5,"https://images.unsplash.com/photo-1626806787461-102c1bfaaea1"},
                {"home-appliances","SC-HOM-003","Dyson V12 Vacuum Cleaner","Dyson","dyson-v12-vacuum-cleaner","Cordless vacuum with powerful suction and laser dust detection.",58900,49900,20,4.7,"https://images.unsplash.com/photo-1558618666-fcd25c85cd64"},
                {"kitchen","SC-KIT-001","Philips Digital Air Fryer","Philips","philips-digital-air-fryer","Healthy air fryer with rapid air technology and preset cooking modes.",12995,9495,75,4.6,"https://images.unsplash.com/photo-1612203985729-70726954388c"},
                {"kitchen","SC-KIT-002","Prestige Mixer Grinder 750W","Prestige","prestige-mixer-grinder-750w","Durable mixer grinder with stainless steel jars and powerful motor.",5995,3995,130,4.3,"https://images.unsplash.com/photo-1570222094114-d054a817e56b"},
                {"kitchen","SC-KIT-003","Wonderchef Non-Stick Cookware Set","Wonderchef","wonderchef-nonstick-cookware-set","Premium non-stick cookware set for everyday Indian cooking.",6999,4499,85,4.2,"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136"},
                {"books","SC-BOK-001","Clean Code","Robert C. Martin","clean-code-book","Practical handbook for writing maintainable professional software.",799,599,200,4.8,"https://images.unsplash.com/photo-1515879218367-8466d910aaa4"},
                {"books","SC-BOK-002","Atomic Habits","James Clear","atomic-habits-book","Bestselling guide to building good habits and breaking bad ones.",699,449,260,4.9,"https://images.unsplash.com/photo-1512820790803-83ca734da794"},
                {"books","SC-BOK-003","The Psychology of Money","Morgan Housel","psychology-of-money-book","Timeless lessons on wealth, greed, happiness, and financial behavior.",499,349,240,4.7,"https://images.unsplash.com/photo-1544947950-fa07a98d237f"},
                {"gaming","SC-GAM-001","Logitech G Pro Gaming Mouse","Logitech","logitech-g-pro-gaming-mouse","Lightweight esports mouse with HERO sensor and programmable buttons.",6995,5495,95,4.6,"https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7"},
                {"gaming","SC-GAM-002","Razer BlackWidow Keyboard","Razer","razer-blackwidow-keyboard","Mechanical RGB gaming keyboard with tactile switches and macro support.",11999,8999,60,4.5,"https://images.unsplash.com/photo-1541140532154-b024d705b90a"},
                {"gaming","SC-GAM-003","Sony PlayStation 5 Console","Sony","sony-playstation-5-console","Next-generation gaming console with ultra-fast SSD and 4K gameplay.",54990,49990,30,4.9,"https://images.unsplash.com/photo-1606813907291-d86efa9b94db"},
                {"gaming","SC-GAM-004","Xbox Wireless Controller","Microsoft","xbox-wireless-controller","Ergonomic wireless controller with textured grip and low-latency input.",5990,4990,110,4.4,"https://images.unsplash.com/photo-1600080972464-8e5f35f63d08"}
        };

        for (Object[] p : products) {
            seedProduct(productRepository, categories.get(p[0]), (String) p[1], (String) p[2], (String) p[3], (String) p[4], (String) p[5], p[6].toString(), p[7].toString(), (int) p[8], p[9].toString(), (String) p[10]);
        }
    }

    private Role role(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
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

    private void attachDefaultAddress(User user) {
        Address address = user.getAddresses().stream().filter(Address::isDefaultAddress).findFirst()
                .orElseGet(() -> user.getAddresses().stream().findFirst().orElseGet(() -> {
                    Address created = new Address();
                    created.setUser(user);
                    user.getAddresses().add(created);
                    return created;
                }));
        String fullName = user.getFirstName() + " " + user.getLastName();
        address.setFullName(fullName);
        address.setRecipientName(fullName);
        address.setPhoneNumber(user.getPhone());
        address.setLegacyPhone(user.getPhone());
        address.setAddressLine1("42 MG Road");
        address.setLegacyLine1("42 MG Road");
        address.setAddressLine2("Near Metro Station");
        address.setLegacyLine2("Near Metro Station");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPostalCode("560001");
        address.setCountry("India");
        address.setAddressType(com.swiftcart.enums.AddressType.HOME);
        address.setLabel("Home");
        user.getAddresses().forEach(existing -> {
            existing.setDefaultAddress(false);
            existing.setLegacyDefaultAddress(false);
        });
        address.setDefaultAddress(true);
        address.setLegacyDefaultAddress(true);
    }
    private Category saveCategory(CategoryRepository categoryRepository, String name, String slug, String imageUrl) {
        Category category = categoryRepository.findBySlug(slug).orElseGet(Category::new);
        category.setName(name);
        category.setSlug(slug);
        category.setImageUrl(imageUrl);
        category.setActive(true);
        return categoryRepository.save(category);
    }

    private void seedProduct(ProductRepository productRepository, Category category, String sku, String name, String brand, String slug,
                             String description, String price, String salePrice, int stockQuantity, String rating, String imageUrl) {
        Product product = productRepository.findBySlug(slug).orElseGet(Product::new);
        product.setCategory(category);
        product.setSku(sku);
        product.setName(name);
        product.setBrand(brand);
        product.setSlug(slug);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setSalePrice(new BigDecimal(salePrice));
        product.setStockQuantity(stockQuantity);
        product.setAverageRating(new BigDecimal(rating));
        product.setReviewCount(50 + Math.abs(sku.hashCode() % 250));
        product.setFeatured(sku.endsWith("001"));
        product.setTrending(sku.endsWith("002") || sku.endsWith("003"));
        product.setBestSeller(sku.endsWith("001") || sku.endsWith("004"));
        product.setNewArrival(sku.endsWith("003"));
        product.setBadge(product.getStockQuantity() < 35 ? "LIMITED STOCK" : product.getSalePrice().compareTo(product.getPrice()) < 0 ? "SALE" : "NEW");
        product.setImageUrl(imageUrl);
        product.setActive(true);
        if (product.getImages().isEmpty()) {
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(imageUrl);
            image.setAltText(name);
            image.setMainImage(true);
            image.setPrimaryImage(true);
            image.setSortOrder(1);
            product.getImages().add(image);
        }
        productRepository.save(product);
    }
}



