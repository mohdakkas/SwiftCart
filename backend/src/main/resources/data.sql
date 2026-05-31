INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_USER')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO users (id, email, password, first_name, last_name, phone, enabled, email_verified) VALUES
(1, 'admin@swiftcart.com', '$2a$10$O1mV0Nv35h1pDQ.XjLquVuJgKVjzDlcoL5L4uJ.1G0a/oC.pfgWlG', 'SwiftCart', 'Admin', '9999999999', true, true),
(2, 'maya@swiftcart.com', '$2a$10$3jDhR22rvgYH9ejsMX5RYOxmEeB5vxJ1ZHPcxRLPchmZ0pZ3Zywpm', 'Maya', 'Rao', '8888888888', true, true)
ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 1), (1, 2), (2, 2);

INSERT INTO categories (id, name, slug, image_url, parent_id, active) VALUES
(1, 'Electronics', 'electronics', 'https://images.unsplash.com/photo-1498049794561-7780e7231661', NULL, true),
(2, 'Fashion', 'fashion', 'https://images.unsplash.com/photo-1445205170230-053b83016050', NULL, true),
(3, 'Mobiles', 'mobiles', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9', 1, true),
(4, 'Sneakers', 'sneakers', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', 2, true)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO products (id, category_id, sku, name, slug, description, price, sale_price, stock_quantity, average_rating, review_count, active) VALUES
(1, 3, 'SC-PHONE-X1', 'SwiftPhone X1', 'swiftphone-x1', 'Flagship phone with OLED display, fast charging, and pro-grade camera.', 69999.00, 62999.00, 75, 4.7, 128, true),
(2, 1, 'SC-BUDS-PRO', 'SwiftBuds Pro', 'swiftbuds-pro', 'Noise cancelling wireless earbuds with spatial audio.', 14999.00, 11999.00, 140, 4.5, 94, true),
(3, 4, 'SC-RUN-AIR', 'RunAir Knit Sneakers', 'runair-knit-sneakers', 'Lightweight everyday sneakers with breathable knit upper.', 6499.00, 4999.00, 210, 4.4, 56, true)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO product_images (product_id, image_url, alt_text, primary_image, sort_order) VALUES
(1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9', 'SwiftPhone front view', true, 1),
(2, 'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1', 'SwiftBuds case', true, 1),
(3, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', 'RunAir sneakers', true, 1);

INSERT INTO addresses (id, user_id, label, recipient_name, phone, line1, city, state, postal_code, country, default_address) VALUES
(1, 2, 'Home', 'Maya Rao', '8888888888', '42 MG Road', 'Bengaluru', 'Karnataka', '560001', 'India', true)
ON DUPLICATE KEY UPDATE recipient_name = VALUES(recipient_name);

INSERT INTO carts (id, user_id) VALUES (1, 2) ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);
INSERT INTO wishlists (id, user_id) VALUES (1, 2) ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);
