# SwiftCart

Enterprise-style full stack e-commerce platform scaffold built with Spring Boot 3, Java 17, MySQL, JWT security, and React 19 + Vite + Tailwind.

## Modules

- `backend` - Spring Boot REST API using clean package boundaries, JPA entities, DTOs, services, controllers, security, OpenAPI, schema, and seed data.
- `frontend` - React storefront/admin experience with routing, Redux Toolkit, React Query, Axios, Tailwind, dark mode, and reusable components.
- `docs` - ER diagram, API notes, Postman collection, testing guide, and deployment guide.

## Run Locally

Install and build the frontend, then copy the Vite build into Spring Boot static resources:

```bash
cd frontend
npm install
npm run build
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/copy-dist-to-spring.ps1
```

Or use the combined script:

```bash
cd frontend
npm run build:spring
```

Run the integrated application:

```bash
cd backend
mvn clean spring-boot:run
```

Frontend: `http://localhost:8080`

Default API: `http://localhost:8080/api/v1`

Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`

## Default Seed Users

- Admin: `admin@swiftcart.com` / `Admin@123`
- Customer: `maya@swiftcart.com` / `User@12345`

Passwords are BCrypt hashes in `backend/src/main/resources/data.sql`.

## Massive Product Catalog

The 200-product MySQL catalog seed is available at:

```text
backend/src/main/resources/sql/swiftcart_catalog_200.sql
```

Load it with a MySQL client:

```bash
mysql -u root -p swiftcart < backend/src/main/resources/sql/swiftcart_catalog_200.sql
```

The script creates parent and child categories, sample admin/user accounts, merchandising columns, product badges, and 200 realistic products with images.
