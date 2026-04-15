# Reflect

Guided weekly review and AI insight platform for working professionals.

**Stack:** Spring Boot 3.3 (Java 21) · Next.js 14 · Neon (PostgreSQL) · Upstash (Redis) · Railway · Vercel

---

## Repository structure

```
reflect-app/
├── .github/workflows/ci.yml      GitHub Actions — CI + Railway deploy
├── docs/adr/                      Architecture Decision Records (ADR-001 to ADR-006)
├── frontend/                      Next.js 14 App Router (Vercel)
├── src/
│   ├── main/java/com/reflect/
│   │   ├── api/                   REST controllers
│   │   ├── mcp/                   MCP adapter layer (Phase 2)
│   │   ├── service/               Business logic
│   │   ├── agent/                 AI agent layer (Phase 3)
│   │   ├── domain/                JPA entities
│   │   ├── repository/            Spring Data JPA interfaces
│   │   ├── scheduler/             @Scheduled jobs
│   │   └── config/                Spring configuration beans
│   └── main/resources/
│       ├── db/migration/          Flyway SQL migrations (V1, V2, V3...)
│       ├── db/seed/               Dev-only seed data (V2 — never runs in prod)
│       ├── application.yml        Base config
│       ├── application-dev.yml    Local dev overrides
│       └── application-prod.yml   Railway production overrides
├── CLAUDE.md                      Claude Code project instructions
├── docker-compose.yml             Local dev: Postgres + Redis + Mailhog
└── pom.xml
```

---

## Local development setup

### Prerequisites

- Java 21 (install via [SDKMAN](https://sdkman.io/): `sdk install java 21-tem`)
- Maven 3.9+ (`mvn --version`)
- Docker Desktop
- IntelliJ IDEA or VS Code with Java extension pack

### Steps

**1. Clone and configure environment**

```bash
git clone https://github.com/your-username/reflect-app.git
cd reflect-app
cp .env.local.example .env.local
# Edit .env.local with your actual values (see comments in the file)
```

**2. Generate JWT keys (first time only)**

```bash
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
# Copy contents into .env.local as JWT_PRIVATE_KEY and JWT_PUBLIC_KEY
rm private.pem public.pem  # Don't leave key files in the repo directory
```

**3. Start local infrastructure**

```bash
docker-compose up -d
# Postgres running on localhost:5432
# Redis running on localhost:6379
# Mailhog web UI at http://localhost:8025
```

**4. Run the API**

```bash
# Set env vars (or configure IntelliJ Run Configuration with .env.local)
export $(cat .env.local | xargs)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# API at http://localhost:8080
# Health check: http://localhost:8080/actuator/health
```

Flyway runs automatically on startup and applies V1, V2 (dev seed), and V3 migrations.
The seed data creates one Pro user: `jeffrey@reflect.app` / `reflect123`.

**5. Run tests**

```bash
# Unit tests only (fast — no Docker required)
mvn test

# Full test suite including integration tests (requires Docker for Testcontainers)
mvn verify

# Coverage report at target/site/jacoco/index.html
```

---

## Deployment

### Railway (API)

Push to `main` → GitHub Actions CI passes → Railway automatically deploys.

Production deploy requires manual approval in GitHub Environments.

Railway auto-detects the Maven project and builds with:
```bash
mvn clean package -DskipTests
java -jar target/reflect-api-*.jar
```

Set `SPRING_PROFILES_ACTIVE=prod` and all secrets from `.env.local.example` in Railway's environment variable dashboard.

### Vercel (Frontend)

Push to `main` → Vercel automatically deploys from `frontend/` directory.
Preview deployments created for every PR.

Set `NEXT_PUBLIC_API_URL` to `https://api.reflect.app` in Vercel environment variables.

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
| PRD v1.2 | `/docs/reflect-prd-v1.2.docx` |
| System Design Document v1.2 | `/docs/reflect-sdd-v1.2.docx` |
| Design System v1.0 | `/docs/reflect-design-system-v1.0.docx` |
| Business Plan v1.0 | `/docs/reflect-business-plan.docx` |
| Financial Tracker | `/docs/reflect-financial-tracker.xlsx` |

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
