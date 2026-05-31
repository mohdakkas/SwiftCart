# SwiftCart API Overview

Base URL: `/api/v1`

Swagger UI: `/api/v1/swagger-ui.html`

## Auth

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| POST | `/auth/register` | Create account and send verification token | `201 Created` |
| POST | `/auth/login` | Authenticate and issue JWT pair | `200 OK` |
| POST | `/auth/refresh` | Rotate access token | `200 OK` |
| POST | `/auth/forgot-password` | Issue reset token | `202 Accepted` |
| POST | `/auth/reset-password` | Reset password | `200 OK` |

## Catalog

| Method | Path | Description |
| --- | --- | --- |
| GET | `/products` | Search, filter, sort, and paginate products |
| GET | `/products/{id}` | Product detail |
| GET | `/categories` | Category tree |
| POST | `/admin/products` | Create product |

## Commerce

| Method | Path | Description |
| --- | --- | --- |
| GET | `/cart` | Current cart |
| POST | `/cart/items` | Add item |
| PATCH | `/cart/items/{id}` | Update quantity |
| DELETE | `/cart/items/{id}` | Remove item |
| GET | `/wishlist` | Current wishlist |
| POST | `/orders` | Place order |
| GET | `/orders` | Order history |
| GET | `/orders/{id}/track` | Tracking timeline |
| POST | `/payments/simulate` | Dummy gateway response |

All mutating request payloads use Bean Validation. Error responses follow:

```json
{
  "timestamp": "2026-05-30T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/products",
  "violations": {
    "name": "must not be blank"
  }
}
```
