# Reflect

Guided weekly review and AI insight platform for working professionals.

**Stack:** Spring Boot 3.3 (Java 21) · Next.js 14 · Neon (PostgreSQL) · Upstash (Redis) · Railway · Vercel

---

## Repository structure

```
Reflect/
├── api/                             Spring Boot 3.3 (Java 21) backend
│   ├── src/main/java/com/reflect/
│   │   ├── config/                  Security, JWT, CORS, properties
│   │   ├── controller/              REST controllers + DTOs
│   │   ├── service/                 Business logic
│   │   ├── domain/                  JPA entities
│   │   ├── repository/              Spring Data JPA interfaces
│   │   └── exception/               Error handling
│   ├── src/main/resources/
│   │   ├── db/migration/            Flyway SQL migrations (V1–V3)
│   │   ├── application.yml          Base config
│   │   ├── application-dev.yml      Local dev overrides
│   │   └── application-test.yml     Test profile (Testcontainers)
│   ├── src/test/                    Unit + integration tests
│   └── pom.xml
├── web/                             Next.js 14 (TypeScript) frontend
│   ├── src/app/                     App Router pages
│   ├── src/components/              UI components (Ark UI + Tailwind)
│   ├── src/lib/                     API client, auth provider, types
│   └── package.json
├── docs/                            Project documentation
│   ├── adr/                         Architecture Decision Records
│   ├── reflect-prd-v1.2.md          Product Requirements Document
│   ├── reflect-sdd-v1.2.md          System Design Document
│   ├── reflect-design-system-v1.0.md Design System & Component Library
│   └── superpowers/                 Specs and implementation plans
├── docker-compose.yml               Local dev: Postgres + Redis + MailHog
└── .sdkmanrc                        Auto-switches to Java 21
```

---

## Local development setup

### Prerequisites

- Java 21 (install via [SDKMAN](https://sdkman.io/): `sdk install java 21-tem`)
- Maven 3.9+
- Node.js 18+
- Docker Desktop

### Steps

**1. Clone and generate JWT keys**

```bash
git clone git@github.com:jeffjordan97/Reflect.git
cd Reflect
cd api
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
```

**2. Start local infrastructure**

```bash
cd ..  # back to repo root
docker-compose up -d
# Postgres on localhost:5432
# Redis on localhost:6379
# MailHog web UI at http://localhost:8025
```

**3. Run the backend**

```bash
cd api
export JWT_PRIVATE_KEY="$(cat private.pem)"
export JWT_PUBLIC_KEY="$(cat public.pem)"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# API at http://localhost:8080
# Health check: http://localhost:8080/actuator/health
```

Flyway runs automatically on startup and creates the `users`, `refresh_tokens`, and `check_ins` tables.

**4. Run the frontend** (separate terminal)

```bash
cd web
npm install   # first time only
npm run dev
# Frontend at http://localhost:3000
```

The Next.js dev server proxies `/api/*` requests to the Spring Boot backend.

**5. Run tests**

```bash
cd api

# Unit tests only (fast — no Docker required)
mvn test

# Full test suite including integration tests (requires Docker for Testcontainers)
mvn verify
```

---

## Deployment

### Railway (API)

Set root directory to `api/`. Railway builds with:
```bash
mvn clean package -DskipTests
java -jar target/reflect-api-1.0.0-SNAPSHOT.jar
```

Required environment variables: `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_URL`, `JWT_PRIVATE_KEY`, `JWT_PUBLIC_KEY`.

### Vercel (Frontend)

Set root directory to `web/`. Framework auto-detected as Next.js.

Configure API rewrites to point to the Railway backend URL instead of `localhost:8080`.

---

## Architecture decisions

All major technical decisions are documented in `docs/adr/`:

| ADR | Decision |
|-----|----------|
| [ADR-001](docs/adr/ADR-001-monolith.md) | Monolith over microservices at launch |
| [ADR-002](docs/adr/ADR-002-mcp-controller-layer.md) | MCP adapter as controller layer |
| [ADR-003](docs/adr/ADR-003-api-key-auth.md) | API key auth for MCP (OAuth deferred) |
| [ADR-004](docs/adr/ADR-004-sse-transport.md) | SSE transport for MCP |
| [ADR-005](docs/adr/ADR-005-async-batch-ai.md) | Async batch AI insight generation |
| [ADR-006](docs/adr/ADR-006-nextjs-vercel.md) | Next.js on Vercel for frontend |

Do not re-open a decided ADR. Create a new one if a decision needs revisiting.

---

## Key documents

| Document | Location |
|----------|----------|
| PRD v1.2 | [`docs/reflect-prd-v1.2.md`](docs/reflect-prd-v1.2.md) |
| System Design Document v1.2 | [`docs/reflect-sdd-v1.2.md`](docs/reflect-sdd-v1.2.md) |
| Design System v1.0 | [`docs/reflect-design-system-v1.0.md`](docs/reflect-design-system-v1.0.md) |
| MVP Design Spec | [`docs/superpowers/specs/2026-04-15-reflect-mvp-design.md`](docs/superpowers/specs/2026-04-15-reflect-mvp-design.md) |
| MVP Implementation Plan | [`docs/superpowers/plans/2026-04-15-reflect-mvp.md`](docs/superpowers/plans/2026-04-15-reflect-mvp.md) |

---

## The five check-in prompts (v1.2)

These are fixed. Do not change field names or database column names without a new Flyway migration and PRD update.

| # | Field | Prompt |
|---|-------|--------|
| 1 | `wins` | What moved forward this week — in your work or in how you worked with others? |
| 2 | `friction` | Where did you feel resistance this week — a task, a person, or yourself? |
| 3 | `energy_rating` | Energy slider 1–10 |
| 4 | `signal_moment` | What interaction or moment from this week is still on your mind — and why? |
| 5 | `intentions` | What matters most to you next week — and why does it matter? |
