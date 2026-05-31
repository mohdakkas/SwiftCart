# Deployment Guide

## Local Docker

```bash
docker compose up --build
```

## Integrated Spring Boot Build

```bash
cd frontend
npm install
npm run build
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/copy-dist-to-spring.ps1

cd ../backend
mvn clean spring-boot:run
```

Open `http://localhost:8080`. The React app is served by Spring Boot from `src/main/resources/static`.

## Load Full Catalog

```bash
mysql -u root -p swiftcart < backend/src/main/resources/sql/swiftcart_catalog_200.sql
```

The seed is idempotent and can be rerun after schema updates.

## Backend Production Checklist

- Set `JWT_SECRET` to a strong 256-bit secret.
- Set `SPRING_DATASOURCE_*` from a secret manager.
- Enable HTTPS at the ingress/load balancer.
- Use managed MySQL with automated backups.
- Use Redis for cart/session cache if traffic warrants it.
- Configure SMTP or an event-driven email provider for verification and password reset.

## Frontend Production Checklist

- Set `VITE_API_BASE_URL` to the deployed API URL.
- Build with `npm run build`.
- Serve static assets via CDN-backed object storage or Nginx.
- Configure CSP, HSTS, and cache headers.
