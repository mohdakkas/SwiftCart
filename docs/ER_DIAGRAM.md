# SwiftCart ER Diagram

```mermaid
erDiagram
    USERS ||--o{ ADDRESSES : owns
    USERS ||--|| CARTS : has
    USERS ||--|| WISHLISTS : has
    USERS ||--o{ ORDERS : places
    USERS ||--o{ REVIEWS : writes
    USERS }o--o{ ROLES : granted

    CATEGORIES ||--o{ CATEGORIES : parent_child
    CATEGORIES ||--o{ PRODUCTS : classifies

    PRODUCTS ||--o{ PRODUCT_IMAGES : has
    PRODUCTS ||--o{ PRODUCT_VARIANTS : has
    PRODUCTS ||--o{ REVIEWS : receives
    PRODUCTS ||--o{ ORDER_ITEMS : purchased_as
    PRODUCTS ||--o{ CART_ITEMS : added_as
    PRODUCTS ||--o{ WISHLIST_ITEMS : wished_as

    CARTS ||--o{ CART_ITEMS : contains
    WISHLISTS ||--o{ WISHLIST_ITEMS : contains

    ORDERS ||--o{ ORDER_ITEMS : contains
    ORDERS ||--|| PAYMENTS : paid_by
    ADDRESSES ||--o{ ORDERS : shipping
    ADDRESSES ||--o{ ORDERS : billing

    USERS {
      bigint id PK
      varchar email UK
      varchar password
      varchar first_name
      varchar last_name
      varchar phone
      varchar profile_image_url
      boolean enabled
      boolean email_verified
    }

    PRODUCTS {
      bigint id PK
      varchar sku UK
      varchar name
      text description
      decimal price
      decimal sale_price
      int stock_quantity
      decimal average_rating
      boolean active
    }

    ORDERS {
      bigint id PK
      varchar order_number UK
      enum status
      decimal subtotal
      decimal tax_amount
      decimal shipping_amount
      decimal discount_amount
      decimal total_amount
    }

    PAYMENTS {
      bigint id PK
      varchar transaction_id UK
      enum method
      enum status
      decimal amount
      timestamp payment_date
    }
```

Relationship coverage:

- One-to-one: `User -> Cart`, `User -> Wishlist`, `Order -> Payment`
- One-to-many: `Category -> Product`, `Product -> ProductImage`, `Order -> OrderItem`
- Many-to-one: `Order -> User`, `Product -> Category`, `Review -> Product`
- Many-to-many: `User <-> Role`
