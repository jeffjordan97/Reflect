# Reflect — Project Instructions for Claude

This file configures Claude's behaviour when working in this repository.
Read it fully before making any changes.

---

## Project overview

Reflect is a consumer SaaS application — a guided weekly review tool with AI pattern insights.
The stack is: **Spring Boot 3.3 (Java 21)** API + **Next.js 14** frontend.
Deployed on **Railway** (API), **Vercel** (frontend), **Neon** (PostgreSQL), **Upstash** (Redis).
Source control: **GitHub**. CI/CD: **GitHub Actions**.

See `/docs/` for PRD, SDD, and Design System documents.

---

## Architecture rules

- **Monolith first.** One Spring Boot JAR. Do not propose separate services unless the
  PR description explicitly says a service extraction is in scope.
- **Layer discipline.** Controllers → Services → Repositories. A controller must never
  call a repository directly. A repository must never contain business logic.
- **Agent package.** `com.reflect.agent` contains the four specialist agents
  (PromptAdaptationAgent, PatternSentinelAgent, NudgeAgent, InsightSynthesisAgent).
  Agents call services; they do not call repositories directly.
- **MCP adapter.** `com.reflect.mcp` is a pure translation layer. Zero business logic.
  Every MCP tool call delegates immediately to the service layer.

---

## Database schema (v1.2)

Key field names — use these exactly, never the old names:

| Entity         | Field name     | Note                                        |
|----------------|----------------|---------------------------------------------|
| `check_ins`    | `friction`     | Was `blockers` — captures avoidance + interpersonal resistance |
| `check_ins`    | `signal_moment`| Was `energy_notes` — new prompt 4 ("what interaction is still on your mind?") |
| `pattern_signals` | full table  | Written by PatternSentinelAgent post-submission |

Migrations live in `api/src/main/resources/db/migration/` as Flyway V-files.
**Never modify an existing migration file** — always create a new one.

---

## Coding standards

### Java / Spring Boot

- Java 21. Use records for DTOs. Use `Optional` properly — never `.get()` without a check.
- All service methods that call external APIs (Anthropic, Stripe, SendGrid) must wrap calls
  in try/catch and never let an external API failure propagate as an uncaught exception to
  the controller layer.
- `@Transactional` on service methods that write to multiple tables.
- Return `ResponseEntity<?>` from controllers. Never return raw domain objects — use DTOs.
- Error responses follow this format exactly:
  ```json
  { "error": "ERROR_CODE", "message": "Human-readable", "timestamp": "ISO8601" }
  ```
- Free-tier enforcement: `CheckInService.submitCheckIn()` must call
  `checkEntryLimit(userId)` before saving. This guard must never be bypassed.
- Pro-tier enforcement: all agent methods and MCP tool handlers must call
  `userService.requirePro(userId)` as their first line. Throw `PlanRequiredException`
  if not Pro — never silently downgrade behaviour.

### Naming conventions

- Services: `*Service.java`
- Agents: `*Agent.java`
- Controllers: `*Controller.java`
- DTOs: `*Request.java` (inbound) / `*Response.java` (outbound)
- Repositories: `*Repository.java` (Spring Data JPA interfaces)

### MCP tools

All four tool names are fixed — do not rename them:
- `submit_checkin`
- `get_insights`
- `get_history`
- `get_streak`

Parameters use snake_case to match the MCP spec. Java field names use camelCase
internally; Jackson handles the mapping.

---

## Testing requirements

- **Unit tests:** Every new service method needs a JUnit 5 unit test with Mockito.
  Aim for ≥ 80% service layer coverage. Run with `cd api && mvn test`.
- **Integration tests:** Use `@SpringBootTest` + Testcontainers (Postgres).
  Integration test classes use `*IntegrationTest.java` or `*IT.java` suffix (Failsafe runs these via `mvn verify`).
  Located alongside their unit test counterparts in `api/src/test/java/com/reflect/`.
- **MCP contract tests:** Every MCP tool handler needs a MockMvc test covering:
  - Valid input → 200 with correct JSON-RPC response
  - Missing required field → 400 with VALIDATION_ERROR
  - Free-tier user → 403 with PRO_REQUIRED
  - Pro-tier user → 200
- **Never delete a test** to make a build pass. Fix the code instead.

---

## What the 5 check-in prompts are (v1.2)

When generating sample data, test fixtures, or example content, use these exact prompt
labels and placeholder texts:

| # | Field name      | Label           | Placeholder                                                        |
|---|-----------------|-----------------|---------------------------------------------------------------------|
| 1 | `wins`          | Progress        | What moved forward this week — in your work or how you worked with others? |
| 2 | `friction`      | Friction        | Where did you feel resistance this week — a task, a person, or yourself? |
| 3 | `energy_rating` | Energy          | Slider 1–10 (1 = Very low, 10 = Excellent)                         |
| 4 | `signal_moment` | Signal moment   | What interaction or moment from this week is still on your mind — and why? |
| 5 | `intentions`    | Next week       | What matters most to you next week — and why does it matter?       |

---

## Environment variables

Never hardcode secrets. Always use env vars. The full list is in the SDD Appendix A.
Locally, use `.env.local` (git-ignored). In Railway, set via the Railway dashboard.

Required for local dev (MVP):
```
JWT_PRIVATE_KEY=<generate with: openssl genrsa -out private.pem 2048>
JWT_PUBLIC_KEY=<corresponding public key>
```

Database and Redis URLs are hardcoded in `application-dev.yml` for Docker Compose defaults.
Phase 2+ will add: `ANTHROPIC_API_KEY`, `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `SENDGRID_API_KEY`.

---

## Git workflow

- Branch naming: `feature/<short-desc>`, `fix/<short-desc>`, `chore/<short-desc>`
- PR title format: `[type] Short description` where type is feat / fix / chore / docs
- Commit messages: imperative mood, present tense — "Add signal_moment field" not "Added"
- **Never push directly to main.** Always open a PR and wait for GitHub Actions CI to pass.
- PRs that change the DB schema must include the Flyway migration file in the same PR.

---

## Decisions already made (do not re-open without a new ADR)

| ADR    | Decision                                    |
|--------|---------------------------------------------|
| ADR-001 | Monolith — not microservices              |
| ADR-002 | MCP adapter as controller layer           |
| ADR-003 | API key auth for MCP at launch            |
| ADR-004 | SSE transport for MCP                     |
| ADR-005 | Async batch AI insight generation         |
| ADR-006 | Next.js on Vercel for frontend            |

ADR files are in `/docs/adr/`. If a decision needs revisiting, create a new ADR file —
do not edit an existing one.

---

## Out of scope (do not build unless a PRD update is in scope)

- Native iOS / Android app
- Custom prompt builder
- Third-party calendar integrations (Jira, Linear, Notion) — Phase 4 only
- Real-time AI chat within the web app
- Multi-user / Teams plan — Phase 4 only
- OAuth 2.0 for MCP — Phase 3 only (API key is sufficient for launch)
