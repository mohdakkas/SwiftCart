CREATE DATABASE IF NOT EXISTS swiftcart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE swiftcart;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(180) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(80) NOT NULL,
  last_name VARCHAR(80) NOT NULL,
  phone VARCHAR(30),
  profile_image_url VARCHAR(500),
  email_verification_token VARCHAR(120),
  email_verification_token_expires_at TIMESTAMP NULL,
  password_reset_token VARCHAR(120),
  password_reset_token_expires_at TIMESTAMP NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(128) NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(140) NOT NULL,
  slug VARCHAR(180) NOT NULL UNIQUE,
  image_url VARCHAR(500),
  parent_id BIGINT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  sku VARCHAR(80) NOT NULL UNIQUE,
  name VARCHAR(220) NOT NULL,
  slug VARCHAR(240) NOT NULL UNIQUE,
  description TEXT NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  sale_price DECIMAL(12,2),
  stock_quantity INT NOT NULL DEFAULT 0,
  average_rating DECIMAL(3,2) NOT NULL DEFAULT 0,
  review_count INT NOT NULL DEFAULT 0,
  featured BOOLEAN NOT NULL DEFAULT FALSE,
  trending BOOLEAN NOT NULL DEFAULT FALSE,
  best_seller BOOLEAN NOT NULL DEFAULT FALSE,
  new_arrival BOOLEAN NOT NULL DEFAULT FALSE,
  badge VARCHAR(40),
  image_url VARCHAR(700),
  image_public_id VARCHAR(180),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS product_images (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  image_url VARCHAR(700) NOT NULL,
  image_public_id VARCHAR(180),
  alt_text VARCHAR(220),
  is_main_image BOOLEAN NOT NULL DEFAULT FALSE,
  primary_image BOOLEAN NOT NULL DEFAULT FALSE,
  sort_order INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS product_variants (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  color VARCHAR(80),
  size VARCHAR(80),
  sku VARCHAR(100) NOT NULL UNIQUE,
  price_delta DECIMAL(12,2) NOT NULL DEFAULT 0,
  stock_quantity INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS addresses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  label VARCHAR(80),
  recipient_name VARCHAR(160) NOT NULL,
  phone VARCHAR(30) NOT NULL,
  line1 VARCHAR(220) NOT NULL,
  line2 VARCHAR(220),
  city VARCHAR(120) NOT NULL,
  state VARCHAR(120) NOT NULL,
  postal_code VARCHAR(20) NOT NULL,
  country VARCHAR(80) NOT NULL,
  default_address BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS carts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
  CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS wishlists (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS wishlist_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  wishlist_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists(id),
  CONSTRAINT fk_wishlist_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  shipping_address_id BIGINT NOT NULL,
  billing_address_id BIGINT NOT NULL,
  order_number VARCHAR(80) NOT NULL UNIQUE,
  status VARCHAR(40) NOT NULL,
  subtotal DECIMAL(12,2) NOT NULL,
  tax_amount DECIMAL(12,2) NOT NULL,
  shipping_amount DECIMAL(12,2) NOT NULL,
  discount_amount DECIMAL(12,2) NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_orders_shipping_address FOREIGN KEY (shipping_address_id) REFERENCES addresses(id),
  CONSTRAINT fk_orders_billing_address FOREIGN KEY (billing_address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(220) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  total_price DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS order_tracking (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  status VARCHAR(40) NOT NULL,
  message VARCHAR(500) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_tracking_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL UNIQUE,
  transaction_id VARCHAR(120) NOT NULL UNIQUE,
  method VARCHAR(40) NOT NULL,
  status VARCHAR(40) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  payment_date TIMESTAMP,
  gateway_message VARCHAR(500),
  razorpay_order_id VARCHAR(120),
  razorpay_payment_id VARCHAR(120),
  razorpay_signature VARCHAR(512),
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  rating INT NOT NULL,
  title VARCHAR(180),
  comment TEXT,
  approved BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT uk_reviews_user_product UNIQUE (user_id, product_id)
);




