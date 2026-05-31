const fs = require('fs');
const path = require('path');

const out = path.join(process.cwd(), 'backend/src/main/resources/sql/swiftcart_catalog_200.sql');
fs.mkdirSync(path.dirname(out), { recursive: true });

const esc = (value) => String(value).replace(/'/g, "''");
const slugify = (value) => value.toLowerCase().replace(/&/g, 'and').replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');

const parents = [
  ['Mobiles', 'mobiles'],
  ['Laptops', 'laptops'],
  ['Electronics', 'electronics'],
  ['Fashion', 'fashion'],
  ['Footwear', 'footwear'],
  ['Home & Kitchen', 'home-kitchen'],
  ['Grocery', 'grocery'],
  ['Beauty', 'beauty'],
  ['Books', 'books'],
  ['Gaming', 'gaming']
];

const subcats = {
  mobiles: ['iPhone', 'Samsung', 'OnePlus', 'Xiaomi', 'Realme'],
  laptops: ['MacBook', 'Dell', 'HP', 'Lenovo', 'Asus'],
  electronics: ['Earbuds', 'Smart Watches', 'Speakers', 'Headphones', 'Power Banks'],
  fashion: ['Men Shirts', 'Men T-Shirts', 'Women Dresses', 'Jeans', 'Jackets'],
  footwear: ['Nike', 'Adidas', 'Puma', 'Reebok', 'Skechers'],
  'home-kitchen': ['Mixer', 'Microwave', 'Air Fryer', 'Cookware', 'Water Purifier'],
  grocery: ['Rice', 'Oil', 'Tea', 'Coffee', 'Snacks'],
  beauty: ['Face Wash', 'Shampoo', 'Perfume', 'Skincare', 'Makeup'],
  books: ['Programming', 'Self Help', 'Business', 'Novels', 'Education'],
  gaming: ['Gaming Mouse', 'Keyboard', 'Monitor', 'PlayStation', 'Xbox']
};

const images = {
  mobiles: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9',
  laptops: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853',
  electronics: 'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1',
  fashion: 'https://images.unsplash.com/photo-1445205170230-053b83016050',
  footwear: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff',
  'home-kitchen': 'https://images.unsplash.com/photo-1556911220-bff31c812dba',
  grocery: 'https://images.unsplash.com/photo-1542838132-92c53300491e',
  beauty: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348',
  books: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d',
  gaming: 'https://images.unsplash.com/photo-1593305841991-05c297ba4575'
};

const names = {
  mobiles: ['iPhone 15 Pro 256GB Natural Titanium', 'iPhone 15 128GB Blue', 'iPhone 14 Plus 128GB Midnight', 'iPhone SE 3rd Gen 64GB', 'Samsung Galaxy S24 Ultra 5G', 'Samsung Galaxy S23 FE 5G', 'Samsung Galaxy A55 5G', 'Samsung Galaxy M35 5G', 'OnePlus 12 5G', 'OnePlus 12R 5G', 'OnePlus Nord CE4 5G', 'OnePlus Open Fold', 'Xiaomi 14 Civi 5G', 'Xiaomi Redmi Note 13 Pro+', 'Xiaomi Redmi 13 5G', 'Xiaomi Poco X6 Pro', 'Realme GT 6 5G', 'Realme Narzo 70 Pro', 'Realme 12 Pro+ 5G', 'Realme C65 5G'],
  laptops: ['MacBook Air M3 13-inch 256GB', 'MacBook Air M2 15-inch 512GB', 'MacBook Pro M3 Pro 14-inch', 'MacBook Pro M3 Max 16-inch', 'Dell XPS 13 Plus OLED', 'Dell Inspiron 14 5430', 'Dell G15 Ryzen Gaming Laptop', 'Dell Latitude 7440 Business', 'HP Spectre x360 14', 'HP Pavilion Plus 14', 'HP Victus 16 Gaming', 'HP Envy x360 15', 'Lenovo Yoga Slim 7', 'Lenovo ThinkPad E14 Gen 5', 'Lenovo Legion Slim 5', 'Lenovo IdeaPad Gaming 3', 'Asus Zenbook 14 OLED', 'Asus Vivobook S15 OLED', 'Asus ROG Strix G16', 'Asus TUF Gaming A15'],
  electronics: ['SwiftBuds Pro ANC Earbuds', 'Sony WF-C700N Earbuds', 'Samsung Galaxy Buds FE', 'OnePlus Buds 3', 'Apple AirPods 3rd Gen', 'Apple Watch Series 9 GPS', 'Samsung Galaxy Watch6', 'Noise ColorFit Ultra 3', 'Amazfit GTR Mini', 'Boat Wave Sigma', 'JBL Flip 6 Speaker', 'Sony SRS-XB100 Speaker', 'Marshall Emberton II', 'Boat Stone 1200', 'Zebronics Sound Feast', 'Sony WH-1000XM5 Headphones', 'JBL Tune 770NC Headphones', 'Sennheiser HD 450BT', 'Anker 20000mAh Power Bank', 'Mi 10000mAh Fast Power Bank'],
  fashion: ['Raymond Slim Fit Formal Shirt', 'Louis Philippe Oxford Shirt', 'Peter England Check Shirt', 'Van Heusen Linen Shirt', 'Allen Solly Casual Shirt', 'Levis Graphic Crew T-Shirt', 'US Polo Cotton T-Shirt', 'Puma Active Training T-Shirt', 'H&M Oversized T-Shirt', 'Roadster Henley T-Shirt', 'Zara Floral Midi Dress', 'Only Wrap Summer Dress', 'Aurelia Printed Kurta Dress', 'Mango Satin Evening Dress', 'Biba Cotton A-Line Dress', 'Levis 511 Slim Jeans', 'Wrangler Regular Fit Jeans', 'Pepe Jeans Skinny Denim', 'Nike Sportswear Windrunner Jacket', 'The North Face Puffer Jacket'],
  footwear: ['Nike Air Max Excee Sneakers', 'Nike Revolution 7 Running Shoes', 'Nike Court Vision Low', 'Nike Downshifter 12', 'Adidas Ultraboost Light', 'Adidas Grand Court 2.0', 'Adidas Runfalcon 3.0', 'Adidas Gazelle Originals', 'Puma Smashic Sneakers', 'Puma Softride Enzo Evo', 'Puma Future Rider Play', 'Puma Flyer Runner', 'Reebok Classic Leather', 'Reebok Floatride Energy 5', 'Reebok Zig Dynamica', 'Reebok Club C 85', 'Skechers Go Walk Flex', 'Skechers D-Lites Fresh Start', 'Skechers Arch Fit Road Walker', 'Skechers Max Cushioning Elite'],
  'home-kitchen': ['Philips HL7756 Mixer Grinder', 'Preethi Zodiac 2.0 Mixer', 'Bajaj Rex 750W Mixer', 'Wonderchef Nutri-Blend Mixer', 'Samsung 28L Convection Microwave', 'LG 32L Charcoal Microwave', 'IFB 30L Convection Microwave', 'Panasonic 20L Solo Microwave', 'Philips Digital Air Fryer 4.1L', 'Instant Vortex Plus Air Fryer', 'Havells Prolife Grande Air Fryer', 'Kent Classic Hot Air Fryer', 'Prestige Omega Cookware Set', 'Hawkins Tri-Ply Pressure Cooker', 'Wonderchef Granite Cookware', 'Borosil Stainless Steel Kadai', 'Kent Supreme RO Water Purifier', 'Aquaguard Aura RO UV Purifier', 'Livpure Glo Star Water Purifier', 'Pureit Advanced RO+UV'],
  grocery: ['India Gate Basmati Rice 5kg', 'Daawat Rozana Gold Rice 5kg', 'Fortune Sona Masoori Rice 10kg', 'Organic Tattva Brown Rice 1kg', 'Fortune Sunflower Oil 5L', 'Saffola Gold Oil 5L', 'Dhara Groundnut Oil 1L', 'Figaro Olive Oil 1L', 'Tata Tea Premium 1kg', 'Red Label Natural Care Tea', 'Wagh Bakri Strong Tea 1kg', 'Organic India Tulsi Green Tea', 'Nescafe Classic Coffee 200g', 'Bru Gold Instant Coffee 200g', 'Davidoff Rich Aroma Coffee', 'Sleepy Owl Cold Brew Coffee', 'Haldiram Aloo Bhujia 1kg', 'Bingo Mad Angles Pack', 'Kelloggs Chocos 700g', 'Too Yumm Multigrain Chips'],
  beauty: ['Himalaya Neem Face Wash', 'Cetaphil Gentle Cleanser', 'Mamaearth Vitamin C Face Wash', 'Minimalist Salicylic Face Wash', 'Dove Intense Repair Shampoo', 'Loreal Paris Total Repair Shampoo', 'Tresemme Keratin Smooth Shampoo', 'Biotique Bio Kelp Shampoo', 'Bella Vita Luxury Perfume', 'Engage Amber Hues Perfume', 'Skinn by Titan Raw Perfume', 'Ajmal Wisal Dahn Perfume', 'Neutrogena Hydro Boost Gel', 'The Derma Co Sunscreen SPF 50', 'Plum Green Tea Moisturizer', 'Dot & Key Barrier Repair Cream', 'Maybelline Fit Me Foundation', 'Lakme Eyeconic Kajal', 'Sugar Matte Lipstick', 'Nykaa Eyes On Me Palette'],
  books: ['Clean Code by Robert C. Martin', 'Effective Java 3rd Edition', 'Spring Boot in Action', 'Designing Data-Intensive Applications', 'Atomic Habits', 'The Psychology of Money', 'Deep Work', 'Ikigai', 'Zero to One', 'The Lean Startup', 'Good to Great', 'Rich Dad Poor Dad', 'The Alchemist', 'The Kite Runner', 'Norwegian Wood', 'Project Hail Mary', 'NCERT Science Class 10', 'Quantitative Aptitude by RS Aggarwal', 'Word Power Made Easy', 'Oswaal CBSE Sample Papers'],
  gaming: ['Logitech G502 Hero Gaming Mouse', 'Razer DeathAdder V3 Mouse', 'Redragon Cobra RGB Mouse', 'HyperX Pulsefire Haste Mouse', 'Keychron K2 Mechanical Keyboard', 'Razer BlackWidow V4 Keyboard', 'Logitech G Pro X Keyboard', 'Redragon Kumara K552 Keyboard', 'LG UltraGear 27-inch 144Hz Monitor', 'Samsung Odyssey G5 32-inch Monitor', 'Acer Nitro XV272U Monitor', 'BenQ Zowie XL2411K Monitor', 'Sony PlayStation 5 Disc Edition', 'Sony PlayStation 5 Digital Edition', 'DualSense Wireless Controller', 'PS5 Spider-Man 2 Bundle', 'Xbox Series X 1TB Console', 'Xbox Series S 512GB Console', 'Xbox Wireless Controller Carbon Black', 'Xbox Game Pass Ultimate 3 Months']
};

const descriptions = {
  mobiles: 'Premium 5G smartphone with vivid display, dependable battery life, fast charging, advanced camera tuning, and polished everyday performance.',
  laptops: 'Productivity-focused laptop with modern processor performance, sharp display, fast storage, premium keyboard comfort, and reliable battery backup.',
  electronics: 'Smart everyday electronics accessory built for clear audio, long battery life, reliable connectivity, and easy pairing across devices.',
  fashion: 'Comfortable wardrobe essential with premium fabric, refined stitching, modern fit, and versatile styling for work, travel, and weekends.',
  footwear: 'Durable footwear with cushioned comfort, grippy outsole, breathable upper, and a clean silhouette for daily wear and active routines.',
  'home-kitchen': 'Reliable home appliance designed for efficient cooking, easy cleaning, durable materials, and consistent performance in busy kitchens.',
  grocery: 'Everyday grocery staple selected for quality, freshness, consistent taste, and dependable value for family kitchens.',
  beauty: 'Personal care essential formulated for daily use, a pleasant finish, reliable performance, and a polished grooming routine.',
  books: 'Well-regarded title for focused reading, practical learning, strong ideas, and long-term value for students and professionals.',
  gaming: 'Performance gaming gear with responsive controls, immersive visuals, durable construction, and smooth gameplay for competitive sessions.'
};

const bases = { mobiles: 18000, laptops: 48000, electronics: 1800, fashion: 900, footwear: 1800, 'home-kitchen': 2500, grocery: 250, beauty: 350, books: 299, gaming: 1800 };
const step = { mobiles: 3600, laptops: 5200, electronics: 900, fashion: 350, footwear: 600, 'home-kitchen': 1400, grocery: 90, beauty: 180, books: 120, gaming: 1800 };
const badges = ['SALE', 'NEW', 'TRENDING', 'BEST SELLER', 'LIMITED STOCK'];

let sql = `-- SwiftCart massive catalog seed: 200 products + categories + sample accounts
-- Compatible with MySQL 8 and the Java 17 Spring Boot backend.

CREATE DATABASE IF NOT EXISTS swiftcart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE swiftcart;

ALTER TABLE products ADD COLUMN IF NOT EXISTS featured BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE products ADD COLUMN IF NOT EXISTS trending BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE products ADD COLUMN IF NOT EXISTS best_seller BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE products ADD COLUMN IF NOT EXISTS new_arrival BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE products ADD COLUMN IF NOT EXISTS badge VARCHAR(40);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER') ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO users (email, password, first_name, last_name, phone, enabled, email_verified) VALUES
('admin@swiftcart.com', '$2a$10$O1mV0Nv35h1pDQ.XjLquVuJgKVjzDlcoL5L4uJ.1G0a/oC.pfgWlG', 'SwiftCart', 'Admin', '9999999999', true, true),
('maya@swiftcart.com', '$2a$10$3jDhR22rvgYH9ejsMX5RYOxmEeB5vxJ1ZHPcxRLPchmZ0pZ3Zywpm', 'Maya', 'Rao', '8888888888', true, true)
ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name), phone = VALUES(phone), enabled = VALUES(enabled), email_verified = VALUES(email_verified);

INSERT IGNORE INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_ADMIN' WHERE u.email = 'admin@swiftcart.com';
INSERT IGNORE INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_USER' WHERE u.email IN ('admin@swiftcart.com','maya@swiftcart.com');
INSERT IGNORE INTO carts (user_id) SELECT id FROM users WHERE email IN ('admin@swiftcart.com','maya@swiftcart.com');
INSERT IGNORE INTO wishlists (user_id) SELECT id FROM users WHERE email IN ('admin@swiftcart.com','maya@swiftcart.com');

`;

for (const [name, slug] of parents) {
  sql += `INSERT INTO categories (name, slug, image_url, parent_id, active) VALUES ('${esc(name)}','${slug}','${images[slug]}',NULL,true) ON DUPLICATE KEY UPDATE name=VALUES(name), image_url=VALUES(image_url), active=true;\n`;
  for (const child of subcats[slug]) {
    const childSlug = slugify(child);
    sql += `INSERT INTO categories (name, slug, image_url, parent_id, active) SELECT '${esc(child)}','${childSlug}','${images[slug]}', id, true FROM categories WHERE slug='${slug}' ON DUPLICATE KEY UPDATE name=VALUES(name), image_url=VALUES(image_url), parent_id=VALUES(parent_id), active=true;\n`;
  }
}

sql += '\n';
let counter = 1;
for (const [, parentSlug] of parents) {
  const childSlugs = subcats[parentSlug].map(slugify);
  names[parentSlug].forEach((name, index) => {
    const catSlug = childSlugs[index % childSlugs.length];
    const original = bases[parentSlug] + step[parentSlug] * ((index % 8) + 1) + (index * 37);
    const discount = 8 + (index % 6) * 4;
    const sale = Math.round(original * (100 - discount) / 100);
    const rating = (4.1 + (index % 9) * 0.1).toFixed(1);
    const stock = index % 9 === 0 ? 8 + index : 35 + index * 7;
    const badge = badges[index % badges.length];
    const featured = index % 4 === 0;
    const trending = index % 5 === 1;
    const best = index % 5 === 2;
    const fresh = index % 5 === 3;
    const sku = `SC-${parentSlug.toUpperCase().replace(/-/g, '')}-${String(index + 1).padStart(3, '0')}`;
    const productSlug = slugify(name);
    sql += `INSERT INTO products (category_id, sku, name, slug, description, price, sale_price, stock_quantity, average_rating, review_count, featured, trending, best_seller, new_arrival, badge, active) SELECT id, '${sku}', '${esc(name)}', '${productSlug}', '${esc(descriptions[parentSlug])}', ${original}.00, ${sale}.00, ${stock}, ${rating}, ${65 + counter}, ${featured}, ${trending}, ${best}, ${fresh}, '${badge}', true FROM categories WHERE slug='${catSlug}' ON DUPLICATE KEY UPDATE category_id=VALUES(category_id), name=VALUES(name), description=VALUES(description), price=VALUES(price), sale_price=VALUES(sale_price), stock_quantity=VALUES(stock_quantity), average_rating=VALUES(average_rating), review_count=VALUES(review_count), featured=VALUES(featured), trending=VALUES(trending), best_seller=VALUES(best_seller), new_arrival=VALUES(new_arrival), badge=VALUES(badge), active=true;\n`;
    sql += `INSERT INTO product_images (product_id, image_url, alt_text, primary_image, sort_order) SELECT id, '${images[parentSlug]}', '${esc(name)}', true, 1 FROM products WHERE slug='${productSlug}' AND NOT EXISTS (SELECT 1 FROM product_images pi WHERE pi.product_id = products.id AND pi.primary_image = true);\n`;
    counter++;
  });
}

sql += `\n-- Total products generated: ${counter - 1}\n`;
fs.writeFileSync(out, sql, 'utf8');
console.log(`Generated ${counter - 1} products at ${out}`);
