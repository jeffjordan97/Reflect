# Reflect — System Design Document

| Field | Value |
|-------|-------|
| Document Version | **1.2 — Agent Architecture & Infrastructure Update** |
| Status | Draft |
| Product | Reflect — Guided Weekly Review & AI Insight Platform |
| Author | Jeffrey Jordan |
| Date | April 2026 |
| Companion Docs | PRD v1.2, Business Plan v1.0, Design System v1.0 |

**Change log**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Apr 2026 | Jeffrey Jordan | Initial release including MCP integration design |
| 1.1 | Apr 2026 | Jeffrey Jordan | Multi-agent layer added (Section 3.3). Schema updated: `blockers → friction`, `energy_notes → signal_moment`, `pattern_signals` table added. Model selection updated. |
| 1.2 | Apr 2026 | Jeffrey Jordan | Infrastructure replaced: Vercel + Railway + Neon + Upstash + GitHub Actions throughout. AWS/EC2/RDS/ElastiCache/Bitbucket references removed. `reminder_log` table added (V3 migration). |

> **Audience & Purpose**
>
> This document defines the technical architecture of Reflect. It covers system topology, component design, API contracts, database schema, MCP server design, agent architecture, security, infrastructure, CI/CD, observability, and scaling strategy. It is the primary reference for engineering decisions and is intended for the founding engineer, any future collaborators, and technical reviewers. It is a living document — update it when design decisions change.

---

## 1. System Overview

Reflect is a consumer SaaS product comprising three distinct access surfaces: a web application, a REST API, and an MCP server. All three surfaces share a single Spring Boot backend — the MCP server and REST API are separate controller layers within the same deployable unit, not separate services. This is a deliberate architectural constraint (ADR-001) that keeps the infrastructure simple and the codebase unified for a solo-founder context.

### 1.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT ZONE                              │
│                                                                 │
│   ┌──────────────────┐        ┌───────────────────────────┐    │
│   │  Next.js Web App │        │  MCP-Compatible AI Client │    │
│   │  (Vercel CDN)    │        │  (Claude, Cursor, etc.)   │    │
│   └────────┬─────────┘        └─────────────┬─────────────┘    │
└────────────┼──────────────────────────────────┼─────────────────┘
             │ HTTPS / REST                      │ HTTPS / MCP SSE
┌────────────▼───────────────────────────────────▼─────────────────┐
│                    API ZONE (Railway)                            │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │              Spring Boot Application                    │   │
│   │  ┌──────────────────┐  ┌──────────────────────────────┐ │   │
│   │  │  REST Controllers│  │   MCP Adapter Layer          │ │   │
│   │  └────────┬─────────┘  └──────────────┬───────────────┘ │   │
│   │           └──────────────┬─────────────┘                │   │
│   │               ┌──────────▼──────────┐                   │   │
│   │               │   Service Layer      │                   │   │
│   │               └──────────┬──────────┘                   │   │
│   │       ┌──────────────────┼──────────────────┐           │   │
│   │  ┌────▼────┐    ┌────────▼──────┐  ┌────────▼──────┐   │   │
│   │  │ AuthSvc │    │CheckInService │  │InsightService │   │   │
│   │  └─────────┘    └───────────────┘  └───────────────┘   │   │
│   └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│   ┌─────────────────┐  ┌───────────────┐  ┌───────────────┐    │
│   │  Neon           │  │ Upstash Redis │  │ SendGrid API  │    │
│   │  PostgreSQL 15  │  │               │  │               │    │
│   └─────────────────┘  └───────────────┘  └───────────────┘    │
│                                                                  │
│   ┌─────────────────┐  ┌───────────────┐                        │
│   │  Stripe API     │  │ Anthropic API │                        │
│   └─────────────────┘  └───────────────┘                        │
└──────────────────────────────────────────────────────────────────┘
```

### 1.2 Architecture Style & Key Decisions

Reflect uses a monolithic-first architecture — a single deployable Spring Boot JAR serving both REST and MCP endpoints (ADR-001). The service layer has clear boundaries so that individual services can be extracted in Phase 4 if needed, without major refactoring.

---

## 2. Architecture Decision Records (ADRs)

### ADR-001 — Monolith over Microservices at Launch — ACCEPTED

**Context:** A solo founder with 4–8 hrs/week cannot sustainably operate multiple independent services. Microservices impose significant operational overhead not justified at Reflect's initial scale.

**Decision:** Ship a single Spring Boot JAR containing all business logic. Internal boundaries enforced via package structure and service interfaces. Extract services only when a specific scaling bottleneck is identified.

**Consequences:** Pro: simple deployment, unified logging, no network latency between services. Con: single point of failure, less independent scalability. Accepted: at < 10,000 users, vertical scaling via Railway plan upgrade is the correct first response to load.

---

### ADR-002 — MCP Adapter as Controller Layer, Not Separate Service — ACCEPTED

**Context:** The MCP server needs access to the same service layer as the REST API. A separate MCP service would require inter-service communication and duplicated auth logic.

**Decision:** Implement the MCP adapter as an additional Spring Boot controller package (`com.reflect.mcp`) within the same application. No business logic lives in the MCP package.

**Consequences:** Pro: no code duplication, unified auth, single deployment. Con: MCP and REST share the same thread pool. Mitigation: dedicated async thread pool for MCP SSE connections in `AsyncConfig.java`.

---

### ADR-003 — API Key Authentication for MCP (with OAuth 2.0 Path) — ACCEPTED

**Context:** OAuth 2.0 is ideal but has significant implementation complexity. API keys are simpler and sufficient for launch.

**Decision:** Launch MCP with personal API key authentication (SHA-256 hashed, displayed once on generation). Implement OAuth 2.0 as a Phase 3 enhancement. API keys remain available permanently.

**Consequences:** Pro: simple to implement, well-understood by developers, instantly revocable. Con: no built-in expiry without custom logic. Mitigation: rotation guidance in UI; 90-day rotation recommended.

---

### ADR-004 — SSE Transport for MCP — ACCEPTED

**Context:** MCP supports SSE (stateless per tool call) and WebSocket (persistent connection). SSE is simpler and stateless.

**Decision:** Use SSE transport for MCP v1.0. Spring Boot has first-class SSE support via `SseEmitter`.

**Consequences:** Pro: simpler implementation, works through Railway's load balancer, no connection state. Con: ~50ms higher overhead per call vs WebSocket. Accepted at launch scale.

---

### ADR-005 — Async Batch Processing for AI Insight Generation — ACCEPTED

**Context:** Claude API calls take 5–30 seconds. Blocking user requests is unacceptable per the NFR.

**Decision:** Insight generation runs as a scheduled background job (Spring `@Scheduled`). Results stored in PostgreSQL and cached in Redis (30-day TTL). A Redis lock prevents double-generation.

**Consequences:** Pro: no user-facing latency, controllable API cost, resilient. Con: insight delivery delayed up to 24hrs after month-end. Accepted per PRD Assumption.

---

### ADR-006 — Next.js on Vercel for Frontend — ACCEPTED

**Context:** Options evaluated: Next.js on Vercel, React SPA on Vercel, or server-rendered HTML from Spring Boot.

**Decision:** Use Next.js 14 deployed to Vercel. SSR for SEO-critical pages; client-side rendering for authenticated app.

**Consequences:** Pro: SSR improves SEO, zero infra management, fast global CDN, preview deployments per PR. Con: splits stack across two platforms. Mitigation: `NEXT_PUBLIC_API_URL` is a single env var; switching to self-hosted Next.js on Railway is a 30-minute change.

---

## 3. Component Design

### 3.1 Spring Boot Application Structure

```
com.reflect
├── api/                         # REST controllers (v1/)
│   ├── AuthController.java
│   ├── CheckInController.java
│   ├── InsightController.java
│   ├── BillingController.java
│   └── UserController.java
├── mcp/                         # MCP adapter layer
│   ├── McpServerController.java # SSE endpoint
│   ├── tools/
│   │   ├── SubmitCheckInTool.java
│   │   ├── GetInsightsTool.java
│   │   ├── GetHistoryTool.java
│   │   └── GetStreakTool.java
│   └── McpAuthFilter.java       # API key validation
├── service/                     # Business logic
│   ├── AuthService.java
│   ├── CheckInService.java
│   ├── InsightService.java          # Delegates to InsightSynthesisAgent (Phase 3)
│   ├── BillingService.java          # Stripe webhook handling
│   ├── EmailService.java            # SendGrid integration
│   └── ApiKeyService.java
├── agent/                       # Multi-agent layer (Phase 3)
│   ├── AgentOrchestrator.java
│   ├── PromptAdaptationAgent.java
│   ├── PatternSentinelAgent.java
│   ├── NudgeAgent.java
│   ├── InsightSynthesisAgent.java
│   └── AgentContext.java
├── domain/                      # JPA entities
│   ├── User.java
│   ├── CheckIn.java                 # v1.2: friction + signal_moment fields
│   ├── InsightReport.java
│   ├── PatternSignal.java           # Phase 3
│   ├── Subscription.java
│   └── ApiKey.java
├── repository/                  # Spring Data JPA interfaces
├── scheduler/                   # @Scheduled jobs
│   ├── ReminderEmailJob.java
│   └── (InsightGenerationJob replaced by InsightSynthesisAgent in Phase 3)
├── config/                      # Spring config beans
│   ├── SecurityConfig.java
│   ├── AsyncConfig.java         # Thread pool for MCP SSE
│   ├── CacheConfig.java         # Redis cache names + TTLs
│   ├── AnthropicConfig.java
│   └── ReflectProperties.java   # @ConfigurationProperties binding
└── ReflectApplication.java
```

### 3.2 Service Layer Responsibilities

| Service | Responsibilities | Key Dependencies |
|---------|-----------------|-----------------|
| `AuthService` | User registration, login (JWT generation), password reset, email verification, session validation | UserRepository, BCryptPasswordEncoder, JwtUtil, EmailService |
| `CheckInService` | Create/update/retrieve check-in entries (`wins`, `friction`, `energy_rating`, `signal_moment`, `intentions`); streak calculation; draft persistence; free-tier entry count enforcement. Publishes `CheckInSubmittedEvent` on save. | CheckInRepository, UserRepository, ApplicationEventPublisher |
| `InsightService` | Phase 1–2: direct Claude API batch processing. Phase 3: delegates to InsightSynthesisAgent. Serves cached insights from Redis; handles rating persistence. | CheckInRepository, InsightReportRepository, AnthropicClient, RedisTemplate |
| `BillingService` | Process Stripe webhooks (`checkout.completed`, `invoice.paid`, `customer.subscription.deleted`); upgrade/downgrade user plan; grace period management | SubscriptionRepository, UserRepository, StripeClient |
| `EmailService` | Send transactional emails via SendGrid: Sunday reminders (augmented by PromptAdaptationAgent in Phase 3), monthly digest, billing alerts, nudge emails, welcome emails | SendGridClient, UserRepository, InsightReportRepository |
| `ApiKeyService` | Generate API keys (SHA-256 hashed), validate inbound MCP requests, revoke keys, list active keys per user | ApiKeyRepository, UserRepository |

### 3.3 Agent Layer (Phase 3)

The agent layer introduces four specialist agents coordinated by an `AgentOrchestrator`. This replaces the single-shot monthly batch model with a continuous, proactive system.

```
CheckInService
      │
      │ publishes CheckInSubmittedEvent
      ▼
AgentOrchestrator  ──────────────────────────────────────────
      │                    │                    │
      ▼                    ▼                    ▼
PatternSentinelAgent  NudgeAgent          InsightSynthesisAgent
  (runs immediately)  (if signal          (runs on schedule,
                       threshold met)      consumes signals)

ReminderEmailJob
      │
      │ calls before sending
      ▼
PromptAdaptationAgent
  (Thursday weekly,
   appends contextual hint
   to Sunday reminder)
```

| Agent | Trigger | Claude Model | Rate Limit / Guard | Output |
|-------|---------|-------------|-------------------|--------|
| `PromptAdaptationAgent` | Thursday `@Scheduled` (weekly), before ReminderEmailJob | `claude-haiku-4-5` | 1 call/user/week max. Skip if no avoidance pattern detected | Optional sentence appended to Sunday reminder email |
| `PatternSentinelAgent` | Spring `@EventListener` on `CheckInSubmittedEvent` | None (rule-based first; Claude only if `signal_strength > 6`) | Must complete within 2 seconds | PatternSignal rows; triggers NudgeAgent if `strength > 7` |
| `NudgeAgent` | `PatternSignalCreatedEvent` from PatternSentinelAgent | `claude-haiku-4-5` | Hard 7-day cooldown per user; Pro users only | In-app notification + optional email. Max 140 chars. |
| `InsightSynthesisAgent` | 1st of month 02:00 UTC `@Scheduled` | `claude-sonnet-4-6` | Sequential per user (1s sleep); Redis lock prevents double-run | InsightReport with `interpersonal_themes`, `avoidance_signals`, pattern context |

**PatternSentinelAgent — Rule-Based Detection Logic:**

```
// Rule-based detection (no API call — runs in-process)
RECURRING_FRICTION:    same friction keyword appears in 3+ of last 4 entries
ENERGY_DROP:           energy_rating drops 3+ points vs. 4-week rolling average
AVOIDANCE:             signal_moment word count < 30 chars for 3+ consecutive weeks
                       OR wins word count < 50 chars for 2+ consecutive weeks

// Claude API escalation (only if signal_strength > 6 after rule check)
INTERPERSONAL:         signal_moment entries contain relational language detected
                       by a lightweight Claude Haiku classification call:
                       'Classify the following text as INTERPERSONAL or PERSONAL.
                        Respond with one word.' Cost: ~£0.0001 per call.

// signal_strength calculation
strength = (frequency_score * 0.4) + (recency_score * 0.4) + (severity_score * 0.2)
// All scores 1–10. strength > 7 triggers NudgeAgent.
```

### 3.4 Scheduled Jobs & Agent Triggers

| Job / Agent | Schedule / Trigger | Description | Failure Handling |
|------------|-------------------|-------------|-----------------|
| `ReminderEmailJob` | `0 0 9 ? * SUN` (timezone-aware) | For each user who has not submitted this week's check-in: send Sunday reminder. Phase 3: calls PromptAdaptationAgent first. | SendGrid delivery errors logged. Idempotency via `reminder_log` table. |
| `PromptAdaptationAgent` | `0 0 4 ? * THU` | Phase 3. For each Pro user: analyse last 4 check-ins for avoidance patterns. | If Claude API unavailable, reminder sends without adaptation (graceful degradation). |
| `PatternSentinelAgent` | Spring `@EventListener` (async) on `CheckInSubmittedEvent` | Phase 3. Runs within 2 seconds of check-in submission. Rule-based detection first. | Async — failure does not affect check-in save confirmation. Errors logged. |
| `NudgeAgent` | Spring `@EventListener` on `PatternSignalCreatedEvent` | Phase 3. Generates specific nudge. Enforces 7-day cooldown. | 7-day cooldown enforced in DB — failure here is non-critical. |
| `InsightSynthesisAgent` | `0 0 2 1 * ?` | Phase 3. Replaces InsightGenerationJob. Richer prompt including signal_moment and PatternSignal history. | CloudWatch alarm on failure. Failed users written to retry queue. Manual retry via `/admin/insights/retry`. |

---

## 4. REST API Design

### 4.1 API Conventions

- Base path: `/api/v1/`
- Authentication: Bearer token (JWT) in `Authorization` header for all protected endpoints
- Content-Type: `application/json` for all request/response bodies
- Error format: `{ "error": "ERROR_CODE", "message": "Human-readable", "timestamp": "ISO8601" }`
- Pagination: cursor-based using `?cursor=<id>&limit=<n>` where applicable
- Versioning: URL path versioning (`/api/v1/`); breaking changes increment version
- All timestamps returned in ISO 8601 UTC format

### 4.2 Authentication Endpoints

| Method | Path | Auth | Description | Request Body | Response |
|--------|------|------|-------------|-------------|---------|
| POST | `/auth/register` | None | Create account | `{ email, password }` | 201: `{ userId, message }` |
| GET | `/auth/verify` | None | Verify email via token | `?token=<jwt>` | 200 or 400 |
| POST | `/auth/login` | None | Authenticate, receive JWT | `{ email, password }` | 200: `{ accessToken, expiresIn }` |
| POST | `/auth/refresh` | JWT | Refresh access token | `{ refreshToken }` | 200: `{ accessToken }` |
| POST | `/auth/forgot-password` | None | Send reset email | `{ email }` | 202 (always, to prevent enumeration) |
| POST | `/auth/reset-password` | None | Reset with token | `{ token, newPassword }` | 200 or 400 |

### 4.3 Check-In Endpoints

| Method | Path | Auth | Description | Notes |
|--------|------|------|-------------|-------|
| GET | `/checkins/current-week` | JWT | Get this week's check-in (draft or submitted) | Returns 404 if no entry exists yet this week |
| POST | `/checkins` | JWT | Submit or update this week's check-in | Idempotent on (userId, weekStartDate). 403 if free user over limit. |
| GET | `/checkins` | JWT | List check-in history | `?cursor=<id>&limit=12`. Free: max 4. Pro: unlimited. |
| GET | `/checkins/streak` | JWT | Get current streak count and last submission date | Returns `{ streak, lastCheckin, nextDue }` |
| GET | `/checkins/stats` | JWT (Pro) | Aggregated trend data for dashboard charts | Returns energy + output time-series arrays |

**Request body for `POST /checkins` (v1.2):**
```json
{
  "wins": "string",
  "friction": "string",
  "energy_rating": 7,
  "signal_moment": "string (optional)",
  "intentions": "string"
}
```

### 4.4 Insights Endpoints

| Method | Path | Auth | Description | Notes |
|--------|------|------|-------------|-------|
| GET | `/insights` | JWT (Pro) | List insight reports | `?period=monthly\|quarterly&n=4`. 403 for free users. |
| GET | `/insights/latest` | JWT (Pro) | Get most recent monthly insight | Returns 404 if no report generated yet |
| POST | `/insights/{id}/rating` | JWT (Pro) | Submit thumbs up/down rating | Body: `{ rating: 'POSITIVE'\|'NEGATIVE' }` |

### 4.5 API Keys Endpoints (MCP)

| Method | Path | Auth | Description | Notes |
|--------|------|------|-------------|-------|
| POST | `/api-keys` | JWT (Pro) | Generate a new API key | Returns plaintext key ONCE. Stored as SHA-256 hash. |
| GET | `/api-keys` | JWT (Pro) | List active API keys (label + last used) | Never returns the key value itself |
| DELETE | `/api-keys/{id}` | JWT (Pro) | Revoke an API key | Immediate invalidation |

### 4.6 Billing Endpoints

| Method | Path | Auth | Description | Notes |
|--------|------|------|-------------|-------|
| POST | `/billing/checkout` | JWT | Create Stripe Checkout session | Body: `{ plan: 'monthly'\|'annual' }`. Returns `{ checkoutUrl }`. |
| POST | `/billing/portal` | JWT (Pro) | Create Stripe Customer Portal session | Returns `{ portalUrl }` |
| POST | `/billing/webhook` | Stripe-Sig | Receive Stripe webhook events | Validates `Stripe-Signature` header. Idempotent event processing. |

---

## 5. MCP Server Design

### 5.1 Transport & Protocol

The Reflect MCP server implements MCP v1.0 using SSE transport (ADR-004).

- **Endpoint:** `https://mcp.reflect.app/v1/messages`
- **Transport:** SSE (Server-Sent Events)
- **Authentication:** API key in `Authorization: Bearer` header (ADR-003)
- **Protocol:** JSON-RPC 2.0 per MCP specification

### 5.2 MCP Tool Implementations

#### Tool 1: `submit_checkin`

```json
{
  "name": "submit_checkin",
  "description": "Submit or update the current week's check-in. Idempotent — safe to call multiple times before Monday 23:59 UTC. Returns the saved entry and current streak.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "wins":          { "type": "string", "description": "What moved forward this week. Free text, 1–2000 chars." },
      "friction":      { "type": "string", "description": "Where you felt resistance — a task, a person, or yourself. Free text, 1–2000 chars." },
      "energy_rating": { "type": "integer", "minimum": 1, "maximum": 10 },
      "signal_moment": { "type": "string", "description": "What interaction or moment is still on your mind — and why. Optional, max 2000 chars." },
      "intentions":    { "type": "string", "description": "What matters most next week — and why does it matter. Free text, 1–2000 chars." }
    },
    "required": ["wins", "friction", "energy_rating", "intentions"]
  }
}
```

**Success response:** `{ "success": true, "week": "2026-04-06", "streak": 14, "message": "Check-in saved." }`

**Error responses:**
- `{ "error": "PRO_REQUIRED", "upgrade_url": "https://reflect.app/upgrade" }` — 403
- `{ "error": "VALIDATION_ERROR", "field": "energy_rating", "message": "Must be 1-10" }` — 400

---

#### Tool 2: `get_insights`

```json
{
  "name": "get_insights",
  "description": "Retrieve AI-generated insight reports. Returns structured insight data including interpersonal themes.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "period": { "type": "string", "enum": ["monthly", "quarterly"] },
      "n":      { "type": "integer", "minimum": 1, "maximum": 4, "default": 1 }
    },
    "required": ["period"]
  }
}
```

**Response content\[0\].text:**
```json
{
  "reports": [{
    "period": "monthly",
    "period_start": "2026-03-01",
    "summary": "March was a high-output month...",
    "patterns": ["High-meeting weeks correlate with lower energy ratings"],
    "interpersonal_themes": ["Manager feedback loop resolved through direct address"],
    "recommendation": "Protect one meeting-free morning block per week.",
    "generated_at": "2026-04-01T02:14:00Z"
  }]
}
```

---

#### Tool 3: `get_history`

```json
{
  "name": "get_history",
  "description": "Retrieve recent check-in entries as structured data.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "n": { "type": "integer", "minimum": 1, "maximum": 12, "default": 4 }
    }
  }
}
```

**Response:** Array of `{ week_start, wins, friction, energy_rating, signal_moment, intentions, submitted_at }`

---

#### Tool 4: `get_streak`

```json
{
  "name": "get_streak",
  "description": "Returns the current check-in streak, last submission date, and when the next check-in is due.",
  "inputSchema": { "type": "object", "properties": {} }
}
```

**Response:** `{ "streak": 14, "last_checkin": "2026-04-06T10:22:00Z", "next_due": "2026-04-13" }`

---

### 5.3 API Key Authentication Flow

```
# Key generation (user action in Settings > Integrations)
POST /api/v1/api-keys  { "label": "My Claude connector" }
→ 201: { "id": "ak_123", "key": "rfl_sk_abc...xyz" }   # shown ONCE
→ DB: { id, user_id, key_hash: SHA256(key), label, created_at }

# MCP tool call (AI client)
POST /mcp/v1/messages
Authorization: Bearer rfl_sk_abc...xyz

# McpAuthFilter validation
1. Extract raw key from header
2. Compute SHA256(rawKey)
3. Check Redis: apikey:{keyHash} → userId (24hr TTL)
4. If Redis miss: SELECT * FROM api_keys WHERE key_hash = ? AND revoked_at IS NULL
5. If not found → 401 Unauthorized
6. If found → load User, check plan == PRO
7. If plan != PRO → 403 { error: PRO_REQUIRED }
8. Set SecurityContext with authenticated user
9. UPDATE api_keys SET last_used_at = NOW()
10. Continue to McpServerController
```

---

## 6. Database Design

### 6.1 Schema

```sql
-- ── Extensions ────────────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── Users ─────────────────────────────────────────────────────────────
CREATE TABLE users (
    id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email              VARCHAR(255) UNIQUE NOT NULL,
    password_hash      VARCHAR(255) NOT NULL,
    email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    plan               VARCHAR(10)  NOT NULL DEFAULT 'FREE' CHECK (plan IN ('FREE', 'PRO')),
    stripe_customer_id VARCHAR(255),
    timezone           VARCHAR(50)  NOT NULL DEFAULT 'UTC',
    reminder_time      TIME         NOT NULL DEFAULT '09:00',
    reminder_enabled   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ── Check-ins (v1.2: friction + signal_moment) ────────────────────────
CREATE TABLE check_ins (
    id              UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    week_start_date DATE      NOT NULL,
    wins            TEXT,
    friction        TEXT,                         -- renamed from 'blockers'
    energy_rating   SMALLINT  CHECK (energy_rating BETWEEN 1 AND 10),
    signal_moment   TEXT,                         -- NEW: interpersonal/awareness prompt
    intentions      TEXT,
    is_draft        BOOLEAN   NOT NULL DEFAULT TRUE,
    submitted_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, week_start_date)
);

-- ── Insight reports ───────────────────────────────────────────────────
-- content JSONB structure (v1.2):
-- {
--   "summary": "string",
--   "patterns": ["string"],
--   "interpersonal_themes": ["string"],   -- from signal_moment entries
--   "avoidance_signals": ["string"],      -- from PatternSignal AVOIDANCE rows
--   "recommendation": "string"
-- }
CREATE TABLE insight_reports (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    period_type  VARCHAR(12) NOT NULL CHECK (period_type IN ('MONTHLY', 'QUARTERLY')),
    period_start DATE        NOT NULL,
    content      JSONB       NOT NULL,
    user_rating  VARCHAR(10) CHECK (user_rating IN ('POSITIVE', 'NEGATIVE')),
    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, period_type, period_start)
);

-- ── Subscriptions ─────────────────────────────────────────────────────
CREATE TABLE subscriptions (
    id                     UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID        UNIQUE NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    stripe_subscription_id VARCHAR(255) UNIQUE NOT NULL,
    plan                   VARCHAR(10) NOT NULL CHECK (plan IN ('MONTHLY', 'ANNUAL')),
    status                 VARCHAR(20) NOT NULL,
    current_period_end     TIMESTAMPTZ,
    cancelled_at           TIMESTAMPTZ,
    grace_period_ends_at   TIMESTAMPTZ,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── API Keys (MCP authentication) ─────────────────────────────────────
CREATE TABLE api_keys (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    key_hash     VARCHAR(64) UNIQUE NOT NULL,
    label        VARCHAR(100),
    last_used_at TIMESTAMPTZ,
    revoked_at   TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Pattern signals (Phase 3 — PatternSentinelAgent) ──────────────────
CREATE TABLE pattern_signals (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    signal_type     VARCHAR(25) NOT NULL CHECK (signal_type IN (
                        'RECURRING_FRICTION', 'ENERGY_DROP', 'AVOIDANCE', 'INTERPERSONAL'
                    )),
    signal_text     TEXT        NOT NULL,
    strength        SMALLINT    NOT NULL CHECK (strength BETWEEN 1 AND 10),
    week_start_date DATE        NOT NULL,
    nudge_sent_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Reminder log (email deduplication) ───────────────────────────────
CREATE TABLE reminder_log (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sent_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    email_type VARCHAR(30) NOT NULL CHECK (email_type IN (
                   'SUNDAY_REMINDER', 'MONTHLY_DIGEST', 'NUDGE',
                   'WELCOME', 'BILLING_ALERT', 'PASSWORD_RESET', 'EMAIL_VERIFY'
               ))
);

-- ── Indexes ───────────────────────────────────────────────────────────
CREATE INDEX idx_users_email              ON users(email);
CREATE INDEX idx_checkins_user_week       ON check_ins(user_id, week_start_date DESC);
CREATE INDEX idx_checkins_user_draft      ON check_ins(user_id, is_draft) WHERE is_draft = TRUE;
CREATE INDEX idx_insights_user_period     ON insight_reports(user_id, period_type, period_start DESC);
CREATE INDEX idx_apikeys_hash             ON api_keys(key_hash) WHERE revoked_at IS NULL;
CREATE INDEX idx_apikeys_user             ON api_keys(user_id) WHERE revoked_at IS NULL;
CREATE INDEX idx_signals_user             ON pattern_signals(user_id, created_at DESC);
CREATE INDEX idx_signals_nudge            ON pattern_signals(user_id, nudge_sent_at) WHERE nudge_sent_at IS NOT NULL;
CREATE INDEX idx_reminder_log_user_week   ON reminder_log(user_id, email_type, sent_at DESC);
```

### 6.2 Entity Relationships

```
users ─────┬──< check_ins              (1 user : many check-ins)
           ├──< insight_reports        (1 user : many reports)
           ├──< pattern_signals        (1 user : many agent-detected signals) [Phase 3]
           ├──○ subscriptions          (1 user : 0-1 subscription)
           ├──< api_keys               (1 user : many API keys)
           └──< reminder_log           (1 user : many email log entries)
```

### 6.3 Data Access Patterns & Caching Strategy

| Query | Frequency | Strategy | Cache TTL |
|-------|-----------|----------|-----------|
| Load dashboard (streak + recent entries + stats) | High (every login) | Indexed query on (user_id, week_start_date DESC LIMIT 12) | No cache; fast indexed read |
| Get latest monthly insight report | Medium (insight card, MCP get_insights) | Redis key: `insight:{userId}:monthly:latest` | 30 days |
| Get quarterly insight report | Low | Redis key: `insight:{userId}:quarterly:latest` | 90 days |
| Validate API key (MCP auth) | High (every MCP tool call) | Redis key: `apikey:{keyHash}` → userId | 24 hours. Invalidated on revoke. |
| Check free-tier entry count | Medium (every check-in attempt) | `SELECT COUNT(*) FROM check_ins WHERE user_id = ?` — fast on indexed user_id | No cache needed |
| Sunday reminder job: eligible users | Weekly (batch) | `SELECT * FROM users WHERE reminder_enabled = TRUE` — full scan, scheduled off-peak | No cache; batch job |

---

## 7. Security Architecture

### 7.1 Authentication Model

| Surface | Mechanism | Token Type | Expiry | Storage |
|---------|-----------|-----------|--------|---------|
| Web App login | Email + bcrypt password | JWT (RS256) | Access: 1hr; Refresh: 30d | Access: memory; Refresh: httpOnly cookie |
| Web App session | JWT Bearer in Authorization header | JWT | 1 hour | Browser memory (not localStorage) |
| MCP tool calls | API Key (SHA-256 hashed) | API Key string | No expiry (revocable) | DB: key_hash only. Redis: 24hr lookup cache. |
| Stripe webhooks | Stripe-Signature HMAC-SHA256 | Webhook secret | Per-event | Env var on Railway |

### 7.2 JWT Design

```json
// JWT payload — signed with RS256 (asymmetric)
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "plan": "PRO",
  "iat": 1712345678,
  "exp": 1712349278
}
```

Public key available at `/api/v1/.well-known/jwks.json`. Private key stored in Railway environment variables, never in source control.

### 7.3 Security Controls

| Control | Implementation | Scope |
|---------|---------------|-------|
| Password hashing | BCrypt cost factor 12 | Registration, password reset |
| HTTPS everywhere | TLS 1.3 minimum; enforced by Vercel (frontend) and Railway (API); HSTS header set | All surfaces |
| Rate limiting | Spring Boot filter: 10 req/min on auth endpoints; 60 req/hr on MCP per API key | Auth, MCP |
| SQL injection | All queries via Spring Data JPA parameterised queries | All DB access |
| XSS | Next.js React escapes output by default; CSP header set via Vercel headers config | Web frontend |
| CSRF | SameSite=Strict on refresh token cookie; stateless JWT for API calls | Web App |
| Secrets management | All secrets stored as Railway environment variables; never committed to source control | All services |
| API key exposure | Key shown once at generation; never logged, never stored in plaintext, never returned in list endpoints | MCP API keys |
| Stripe PCI compliance | No card data touches Reflect infrastructure; all payment UI hosted by Stripe Checkout | Billing |
| GDPR right to erasure | Hard delete of all user rows on account deletion; cascade deletes all check-ins, insights, API keys | Account deletion |
| Dependency scanning | Dependabot enabled on GitHub repo | CI/CD |

---

## 8. AI Integration Design (Claude API)

Phase 1–2 uses a simple batch model (scheduled `InsightGenerationJob`). Phase 3 replaces this with the multi-agent layer defined in Section 3.3.

### 8.1 Phase 1–2: Batch Insight Generation Flow

```
InsightGenerationJob (1st of month, 02:00 UTC)
│
├── 1. Query: Pro users without a report for last month
│
├── 2. For each eligible user:
│   ├── Fetch last month's check-ins (CheckInRepository)
│   ├── Check Redis: IF insight:{userId}:monthly:{period} EXISTS → skip
│   ├── Build prompt (see 8.2)
│   ├── POST to Anthropic API (claude-haiku-4-5, max_tokens=800)
│   ├── Parse JSON response
│   ├── INSERT into insight_reports
│   ├── SET Redis key insight:{userId}:monthly:{period} TTL 30d
│   ├── Send digest email via EmailService
│   └── Sleep 1s  // respect Anthropic rate limits
│
└── 3. Log job completion metrics
```

### 8.2 Prompt Design

**System prompt (fixed, ~200 tokens):**
```
You are a personal performance coach analysing a professional's weekly reflections.
Your role is to identify genuine patterns in their data — not to be generically
positive. Be specific, direct, and actionable. Always respond with valid JSON only.
No preamble, no markdown, no explanation outside the JSON structure.
```

**User prompt (Phase 3 enriched — includes signal_moment + pattern signals):**
```
Analyse the following weekly check-ins from {month} {year}.
Return a JSON object with this exact structure:
{
  "summary": "2-3 sentences summarising the month",
  "patterns": ["pattern 1", "pattern 2"],
  "interpersonal_themes": ["theme 1"],
  "avoidance_signals": ["signal 1"],
  "recommendation": "one concrete, actionable recommendation"
}

Check-ins:
{week_start}:
  Progress:       {wins}
  Friction:       {friction}
  Energy:         {energy_rating}/10
  Signal moment:  {signal_moment}
  Next week:      {intentions}

Detected patterns this month (from sentinel agent):
{pattern_signals as bullet list}
```

### 8.3 Model Selection & Cost Control

| Report Type / Agent | Model | Max Tokens | Est. Cost/User/Month | Rationale |
|--------------------|-------|-----------|---------------------|-----------|
| Monthly insight (Phase 1–2 batch) | `claude-haiku-4-5` | 800 | ~£0.002 | Sufficient for output-pattern summarisation |
| Monthly insight (Phase 3 agent) | `claude-sonnet-4-6` | 1200 | ~£0.012 | Upgraded: signal_moment + interpersonal_themes require deeper reasoning |
| Quarterly review | `claude-sonnet-4-6` | 1500 | ~£0.025 | Deeper narrative reasoning; 4×/year |
| PatternSentinelAgent (INTERPERSONAL) | `claude-haiku-4-5` | 10 | ~£0.0001 | Single-word classification; fires per check-in only when rule score > 6 |
| PromptAdaptationAgent | `claude-haiku-4-5` | 100 | ~£0.0005 | Short contextual sentence generation; fires Thursday weekly per Pro user with avoidance pattern |
| NudgeAgent | `claude-haiku-4-5` | 60 | ~£0.0002 | 140-char nudge generation; fires at most once per 7 days per user |
| MCP `get_insights` | Served from cache | N/A | £0 | All MCP insight calls return cached DB data |

> At 750 Pro users (Month 12): Phase 3 monthly agent cost ≈ 750 × £0.012 = £9.00. Quarterly: 750 × £0.025 × 4 ÷ 12 = £6.25/mo. Sentinel/Nudge/Adaptation agents combined ≈ £2/mo. Total AI cost ≈ £17.25/mo — within the 5% MRR constraint.

---

## 9. Infrastructure Design

### 9.1 Environment Strategy

The deployment stack is **Vercel** (Next.js frontend), **Railway** (Spring Boot API), **Neon** (PostgreSQL), **Upstash** (Redis), and **GitHub** (source control + CI/CD via GitHub Actions).

| Environment | Purpose | Infrastructure | Deployment Trigger |
|------------|---------|---------------|-------------------|
| local | Development + unit testing | Docker Compose: Postgres 15 + Redis 7 + Mailhog | Developer workstation |
| preview | PR review + integration tests | Railway preview environment (auto-provisioned per PR). Neon branch database. | GitHub PR opened / pushed |
| staging | Pre-release validation | Railway staging service + Neon staging branch + Vercel preview URL | Merge to main branch |
| production | Live user traffic | Railway production service + Neon production database + Vercel production URL | Manual Railway deploy from main |

### 9.2 Production Infrastructure

```
┌─────────────────────────────────────────────────────────┐
│                     CLIENT ZONE                         │
│   Next.js App  ─────────────────────────────────────    │
│   Vercel (global CDN, auto-HTTPS, edge functions)       │
└────────────────────────┬────────────────────────────────┘
                         │ HTTPS
┌────────────────────────▼────────────────────────────────┐
│                  API ZONE (Railway)                     │
│   Spring Boot JAR — auto-deploy from GitHub main        │
│   Region: EU West (Railway managed)                     │
│   HTTPS: Railway-provisioned cert (auto-renewed)        │
│   Custom domain: api.reflect.app                        │
└────┬───────────────────┬────────────────────────────────┘
     │                   │
┌────▼────────┐   ┌──────▼──────────────────────────────┐
│  Neon DB    │   │  External Services                  │
│  PostgreSQL │   │  ├── Stripe (payments)              │
│  Serverless │   │  ├── SendGrid (email)               │
│  (EU region)│   │  ├── Anthropic Claude API           │
│             │   │  └── Upstash Redis (sessions/cache) │
└─────────────┘   └─────────────────────────────────────┘
```

### 9.3 Service Configuration

| Service | Provider | Plan / Config | Cost (launch) | Notes |
|---------|----------|--------------|--------------|-------|
| Frontend | Vercel | Hobby → Pro when needed | £0 → £16/mo | Auto-deploy from GitHub. Preview URLs per PR. |
| API | Railway | Hobby plan, 512MB RAM | £5/mo | Deploys Spring Boot fat JAR. Auto-restart on crash. |
| PostgreSQL | Neon | Free tier (0.5GB) → Launch (10GB) | £0 → £15/mo | Serverless Postgres 15. Branching per PR. |
| Redis / Cache | Upstash | Pay-per-request | £0–5/mo | Serverless Redis; no cluster management. |
| Source control | GitHub | Free | £0 | GitHub Actions for CI/CD. Dependabot. |
| Secrets | Railway env vars | Encrypted at rest | Included | All env vars set in Railway dashboard. |
| Monitoring | Railway metrics + Sentry | Sentry free tier | £0 | Error tracking, performance monitoring. |

### 9.4 Deployment Process

Railway auto-detects the Maven project and builds with Nixpacks:

```bash
# Railway build (automatic)
mvn clean package -DskipTests
java -jar target/reflect-*.jar

# Railway environment variables (set in dashboard):
DATABASE_URL=postgresql://...neon.tech/reflect
REDIS_URL=rediss://...upstash.io:6379
JWT_PRIVATE_KEY=...
ANTHROPIC_API_KEY=sk-ant-...
STRIPE_SECRET_KEY=sk_live_...
STRIPE_WEBHOOK_SECRET=whsec_...
SENDGRID_API_KEY=SG...
SPRING_PROFILES_ACTIVE=prod

# Neon database branching (per PR):
# Each GitHub PR auto-creates a Neon branch for isolated testing.
# Branch connection string injected into Railway preview service.

# Zero-downtime deploy: Railway keeps old instance running
# until new one is healthy before switching traffic.
```

---

## 10. CI/CD Pipeline

### 10.1 GitHub Actions Configuration

```yaml
# .github/workflows/ci.yml
name: CI
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: reflect_test
          POSTGRES_USER: reflect
          POSTGRES_PASSWORD: test
        options: --health-cmd pg_isready
      redis:
        image: redis:7-alpine
        options: --health-cmd 'redis-cli ping'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '21', distribution: 'temurin', cache: 'maven' }
      - name: Build & test
        run: mvn clean verify
        env:
          DATABASE_URL: jdbc:postgresql://localhost:5432/reflect_test
          REDIS_URL: redis://localhost:6379
      - name: Upload coverage
        uses: codecov/codecov-action@v4

  deploy-staging:
    needs: build-and-test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Railway (staging)
        uses: bervProject/railway-deploy@main
        with:
          railway_token: ${{ secrets.RAILWAY_TOKEN }}
          service: reflect-api-staging

  deploy-production:
    needs: deploy-staging
    runs-on: ubuntu-latest
    environment: production    # requires manual approval in GitHub
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Railway (production)
        uses: bervProject/railway-deploy@main
        with:
          railway_token: ${{ secrets.RAILWAY_TOKEN }}
          service: reflect-api-production
```

### 10.2 Test Strategy

| Layer | Framework | Scope | Coverage Target |
|-------|-----------|-------|----------------|
| Unit tests | JUnit 5 + Mockito | Service layer: all business logic methods in isolation (mocked repos and external clients) | ≥ 80% |
| Integration tests | Spring Boot Test + Testcontainers | Controller + Service + Repository stack against a real PostgreSQL container | ≥ 60% on controllers |
| MCP contract tests | JUnit 5 + MockMvc | Each MCP tool handler: valid input, missing required fields, free-tier rejection, Pro-tier success | ≥ 90% on MCP handlers |
| Agent unit tests | JUnit 5 + Mockito | PatternSentinelAgent rule logic; NudgeAgent cooldown enforcement; all signal type detection paths | ≥ 80% on agent layer |
| API smoke tests | Postman / Newman | Critical happy paths against staging: register, login, check-in, Stripe webhook | All critical paths |
| Frontend tests | Vitest + React Testing Library | Component unit tests for check-in form, dashboard charts, paywall screen | ≥ 70% on components |

---

## 11. Observability

### 11.1 Logging

- Structured JSON logging via Logback + logstash-logback-encoder
- Log fields: `timestamp`, `level`, `service`, `traceId`, `userId`, `endpoint`, `durationMs`, `statusCode`
- Log levels: ERROR for user-impacting exceptions; WARN for retryable failures; INFO for business events; DEBUG disabled in production
- Railway streams application logs in real time — accessible via Railway dashboard or CLI (`railway logs --tail`)
- Sentry captures unhandled exceptions with stack traces, release tracking, and performance monitoring
- Log retention: Railway retains 7 days; export to Papertrail or Logtail when exceeding 1k users

### 11.2 Metrics & Alarms

| Metric | Source | Alarm Threshold | Action |
|--------|--------|----------------|--------|
| API p95 latency | Railway metrics | > 1000ms for 5 min | Email alert via Railway alerting |
| API error rate (5xx) | Sentry | > 1% of requests for 5 min | Sentry alert → email |
| Railway service crash | Railway health checks | Any crash / restart | Railway auto-restarts; Sentry captures crash context |
| Neon DB connection errors | Sentry + app logs | > 3 errors in 1 min | Email alert; check Neon console |
| Upstash Redis unavailable | App health check endpoint | Any failure | Graceful degradation to DB fallback; email alert |
| InsightSynthesisAgent fail | Sentry custom event | 1 failure | Email alert — manual retry via `/admin/insights/retry` |
| Stripe webhook errors | Stripe dashboard + Sentry | Any 4xx/5xx | Stripe retries 72hr; Sentry captures details |
| MCP tool call error rate | Sentry | > 5% for 10 min | Email alert |
| **\[Agent\]** NudgeAgent delivery failures | Sentry | > 5% per hour | Email alert |

### 11.3 Key Business Metrics

- `reflect/checkins/submitted` — logged as Sentry custom event per submission
- `reflect/users/upgraded` — Stripe webhook → custom event; tracked in Stripe dashboard natively
- `reflect/insights/generated` — logged per InsightSynthesisAgent run; query `insight_reports` table
- `reflect/mcp/tool-calls` — custom Spring interceptor logs tool name + user plan per call
- `reflect/agents/nudge-sent` — PatternSignal.nudge_sent_at population count per week

---

## 12. Scaling Design

### 12.1 Scaling Triggers & Actions

| Scale Point | Trigger | Action | Expected Cost Impact |
|------------|---------|--------|---------------------|
| 0 → 500 users | Launch (Day 1) | Railway Hobby (512MB RAM). Neon free tier. Upstash free tier. | £5–15/mo |
| 500 → 2k users | Railway memory > 80% or p95 > 700ms | Upgrade Railway to Pro plan. Upgrade Neon to Launch plan (10GB). Upstash scales automatically. | £40–70/mo |
| 2k → 10k users | Sustained Railway resource pressure | Railway Pro with horizontal scaling (multiple replicas). Neon Scale plan. Dedicated Redis. | £150–300/mo |
| 10k+ users | Multi-replica pressure or agent job > 30min | Evaluate migration to AWS ECS or dedicated VPS. Extract InsightSynthesisAgent to standalone Railway service. Neon Business plan. | £500+/mo |

### 12.2 Stateless API Design

The Spring Boot API is designed to be stateless — all session state is encoded in the JWT. Railway can run multiple replicas behind its load balancer without sticky sessions or session replication. The only shared state is Neon PostgreSQL and Upstash Redis, both managed services that scale independently.

### 12.3 MCP Scaling Consideration

MCP SSE connections are long-lived HTTP connections. At scale, many concurrent MCP connections could pressure Railway's connection limit. This is mitigated by a dedicated async thread pool in `AsyncConfig.java` (core: 10, max: 50, queue: 100). If MCP adoption significantly exceeds projections, MCP endpoints can be extracted to a separate Railway service using path-based routing (`/mcp/*` → MCP service).

---

## 13. Error Handling & Resilience

| Scenario | Detection | Response | Recovery |
|----------|-----------|---------|---------|
| Claude API timeout (> 30s) | HTTP client timeout | Mark insight job as FAILED for that user; log to Sentry; do not send digest email | Manual retry via `/admin/insights/retry` or next monthly job |
| Claude API rate limit (429) | HTTP 429 response | Exponential backoff: 1s, 2s, 4s (3 retries). If still 429, mark as FAILED. | Automatic retry on next batch run |
| Stripe webhook duplicate | idempotencyKey already processed in DB | Return 200 immediately without reprocessing | No action needed — idempotent by design |
| Stripe webhook delivery failure | Stripe retries for 72hrs | Endpoint returns 200 on all validated webhooks; 400 on invalid signature | Stripe auto-retries; Sentry captures details |
| Neon connection pool exhaustion | HikariCP timeout exception | 500 returned to client; ERROR log emitted | Auto-recovery when connections free; upgrade Neon plan if sustained |
| Upstash Redis unavailable | Connection exception | Insight cache miss — fall back to DB read; API key cache miss — fall back to DB lookup | Transparent degradation; no user-visible error for most paths |
| MCP tool call with invalid params | Bean validation (`@Valid`) | Return JSON-RPC error: `{ code: -32602, message: "Invalid params", data: { field, reason } }` | Client (AI) surfaces error conversationally; user corrects input |
| Railway service crash | Railway health check | Railway auto-restarts the container; Sentry captures crash context | Typically < 30s restart |
| **\[Agent\]** PatternSentinelAgent failure | Sentry async exception | Failure does not affect check-in save confirmation; error logged | Failed signals retried on next check-in event |
| **\[Agent\]** NudgeAgent failure | Sentry | Nudge not delivered this cycle; pattern signal remains for next run | 7-day cooldown still applies; next nudge attempt after cooldown |

---

## Appendix

### A. External Service Configuration Reference

| Service | Key Config | Where Stored |
|---------|-----------|-------------|
| Anthropic Claude API | `ANTHROPIC_API_KEY`, model IDs per report type | Railway environment variables |
| Stripe | `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, price IDs (monthly + annual) | Railway environment variables |
| SendGrid | `SENDGRID_API_KEY`, template IDs per email type | Railway environment variables |
| Neon (PostgreSQL) | `DATABASE_URL` (full JDBC connection string) | Railway environment variables |
| Upstash (Redis) | `REDIS_URL` (`rediss://` with auth token) | Railway environment variables |
| JWT | `JWT_PRIVATE_KEY` (RS256 PEM), `JWT_PUBLIC_KEY` | Railway environment variables |
| Sentry | `SENTRY_DSN` | Railway environment variables |
| Vercel | `NEXT_PUBLIC_API_URL` (`api.reflect.app`) | Vercel environment variables (project settings) |
| GitHub Actions | `RAILWAY_TOKEN`, `CODECOV_TOKEN` | GitHub repository secrets |

### B. Technology Versions

| Technology | Version | Notes |
|-----------|---------|-------|
| Java | 21 (LTS) | Virtual threads available if needed for MCP SSE concurrency |
| Spring Boot | 3.3.x | Spring MVC, Spring Data JPA, Spring Security, Spring Scheduling |
| Next.js | 14.x | App Router; SSR enabled for SEO-critical pages |
| PostgreSQL | 15.x | Neon managed; serverless with auto-suspend on free tier |
| Redis | 7.x | Upstash managed; serverless, pay-per-request |
| Railway | Latest | API hosting; Nixpacks buildpack for Maven detection |
| Neon | Latest | Serverless PostgreSQL; branching per PR via GitHub Action |
| Upstash | Latest | Serverless Redis; TLS (`rediss://`) natively supported by Lettuce |
| Stripe SDK (Java) | Latest stable | `stripe-java` |
| SendGrid SDK | Latest stable | `sendgrid-java` |
| Testcontainers | Latest stable | PostgreSQL + Redis containers for integration tests |

### C. Glossary

| Term | Definition |
|------|-----------|
| ADR | Architecture Decision Record — a document capturing a significant design decision and its rationale |
| MCP | Model Context Protocol — an open standard for exposing structured tool actions to AI clients |
| SSE | Server-Sent Events — HTTP-based unidirectional streaming used for MCP transport in Reflect |
| JWT | JSON Web Token — a compact, signed token used for stateless authentication |
| SHA-256 | Cryptographic hash function used to store API keys without storing plaintext |
| Testcontainers | Java library that spins up Docker containers (Postgres, Redis) during integration tests |
| Idempotent | An operation that produces the same result regardless of how many times it is called |
| Fat JAR | A single deployable Java archive containing the application code and all dependencies |
| p95 latency | The 95th percentile response time — 95% of requests complete faster than this value |
| WAU | Weekly Active Users — users who complete at least one check-in in a given week |
| PatternSentinelAgent | Agent that runs after each check-in to detect emerging patterns using rule-based logic + Claude |
| signal_moment | The v1.2 prompt 4 field: "What interaction or moment from this week is still on your mind — and why?" |
| friction | The v1.2 renamed prompt 2 field (was 'blockers'): captures resistance from tasks, people, or self |
