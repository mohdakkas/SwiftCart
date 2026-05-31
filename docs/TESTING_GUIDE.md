# Testing Guide

## Backend

```bash
cd backend
mvn test
```

The backend targets JDK 17. Verify with `java -version` before running Maven.

Recommended production test layers:

- Unit tests for services using JUnit 5 and Mockito.
- Slice tests for controllers using `@WebMvcTest`.
- Repository tests using Testcontainers MySQL.
- Security tests for role-protected endpoints and JWT validation.

## Frontend

```bash
cd frontend
npm install
npm run lint
npm run build
```

Recommended additions:

- Vitest + React Testing Library for page/component flows.
- Playwright for checkout, login, product filtering, and admin workflows.
