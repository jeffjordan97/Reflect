# Reflect MVP Design Spec

**Date:** 2026-04-15
**Status:** Approved
**Scope:** Phase 1 — Auth + Check-in (no AI, billing, or emails)

## Overview

Reflect is a guided weekly review platform for working professionals. This spec covers the minimum viable product: user authentication and the weekly check-in flow. The goal is to validate the core loop — register, submit a weekly reflection, review past entries — before adding AI insights, billing, or notifications.

## Project Structure

Monorepo with two independent applications sharing a single git repository.

```
Reflect/
├── api/                            # Spring Boot 3.3 (Java 21)
│   ├── src/main/java/com/reflect/api/
│   │   ├── config/                 # Security, CORS, JWT, properties
│   │   ├── domain/                 # JPA entities
│   │   ├── repository/             # Spring Data JPA repositories
│   │   ├── service/                # Business logic
│   │   ├── controller/             # REST controllers
│   │   └── ReflectApplication.java
│   ├── src/main/resources/
│   │   ├── db/migration/           # Flyway SQL migrations
│   │   ├── application.yml
│   │   └── application-dev.yml
│   ├── src/test/
│   ├── pom.xml
│   └── Dockerfile
├── web/                            # Next.js 14 (TypeScript)
│   ├── src/
│   │   ├── app/                    # App Router pages
│   │   ├── components/             # Shared UI components
│   │   ├── lib/                    # API client, auth helpers
│   │   └── styles/                 # Global styles, Tailwind config
│   ├── package.json
│   └── next.config.js
├── docker-compose.yml              # Postgres + Redis (local dev)
├── .github/workflows/              # CI/CD
└── README.md
```

## Database Schema

Three tables. Flyway owns all migrations — Hibernate DDL auto is set to `validate`.

### users

| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PK, DEFAULT gen_random_uuid() |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| display_name | VARCHAR(100) | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() |

### refresh_tokens

| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PK, DEFAULT gen_random_uuid() |
| user_id | UUID | FK → users(id) ON DELETE CASCADE, NOT NULL |
| token_hash | VARCHAR(255) | UNIQUE, NOT NULL |
| expires_at | TIMESTAMPTZ | NOT NULL |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() |

### check_ins

| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PK, DEFAULT gen_random_uuid() |
| user_id | UUID | FK → users(id) ON DELETE CASCADE, NOT NULL |
| week_start | DATE | NOT NULL (always a Sunday) |
| wins | TEXT | nullable |
| friction | TEXT | nullable |
| energy_rating | SMALLINT | CHECK (1-10) |
| signal_moment | TEXT | nullable |
| intentions | TEXT | nullable |
| completed | BOOLEAN | NOT NULL, DEFAULT FALSE |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() |

**Unique constraint:** `(user_id, week_start)` — one check-in per user per week.

**`week_start` is always a Sunday** — the day the check-in is due. The backend derives this from the current date when creating a check-in.

Text fields are nullable to support saving partial wizard progress incrementally. The `completed` flag marks whether the user finished all 5 steps.

Streaks are derived at read time from the `check_ins` table — no dedicated streak table for MVP.

## API Endpoints

All endpoints prefixed with `/api`. Check-in and user endpoints require a valid JWT in the `Authorization: Bearer` header.

### Auth

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| POST | /api/auth/register | `{ email, password, displayName }` | 201 + `{ accessToken, expiresIn }` + Set-Cookie (refresh token) |
| POST | /api/auth/login | `{ email, password }` | 200 + `{ accessToken, expiresIn }` + Set-Cookie (refresh token) |
| POST | /api/auth/refresh | Cookie sent automatically | 200 + `{ accessToken, expiresIn }` + Set-Cookie (new refresh token) |
| POST | /api/auth/logout | Cookie sent automatically | 204 + Clear-Cookie |

### Check-ins

| Method | Path | Description | Response |
|--------|------|------------|----------|
| GET | /api/check-ins | Paginated list (newest first) `?page=0&size=10` | 200 + paginated list |
| GET | /api/check-ins/current | This week's check-in | 200 or 404 |
| POST | /api/check-ins | Create check-in for current week | 201 |
| PUT | /api/check-ins/{id} | Update/save progress | 200 |

### User

| Method | Path | Description | Response |
|--------|------|------------|----------|
| GET | /api/users/me | Current user profile | 200 |

## Authentication Flow

### Token Strategy

- **Access token:** JWT signed with RS256 (asymmetric keys). 15-minute expiry. Stored in memory on the frontend (not localStorage — immune to XSS, cleared on tab close).
- **Refresh token:** Opaque UUID. Stored as SHA-256 hash in the `refresh_tokens` table. 7-day expiry. Sent to the frontend as an httpOnly cookie (immune to JavaScript access).
- **Token rotation:** Each refresh invalidates the old token and issues a new pair.

### Password Security

- BCrypt with strength 12 in production, strength 4 in development (faster tests).

### Rate Limiting

- 5 attempts per minute on `/api/auth/login` and `/api/auth/register`.

### CORS

- Locked to the frontend origin only.

### Flow

1. **Register/Login:** User submits credentials → backend validates → returns access token in response body + refresh token as httpOnly cookie.
2. **Authenticated requests:** Frontend attaches access token from memory to `Authorization: Bearer` header.
3. **Token refresh:** Frontend detects 401 → sends refresh cookie to `/api/auth/refresh` → receives new tokens → retries original request. Transparent to user.
4. **Logout:** Frontend calls `/api/auth/logout` → backend deletes refresh token row → frontend clears in-memory access token.

## Frontend Architecture

### Tech Stack

- Next.js 14 with App Router (TypeScript)
- Ark UI (headless accessible components)
- Tailwind CSS (utility-first styling)
- Inter or Geist typeface

### Routes

| Path | Purpose |
|------|---------|
| `/` | Redirect to `/check-in` (authenticated) or `/login` (unauthenticated) |
| `/login` | Login form |
| `/register` | Registration form |
| `/check-in` | Wizard if no current week entry, else latest entry view |
| `/check-in/new` | Force new wizard (if current week entry exists but incomplete) |
| `/history` | Timeline feed of past check-ins |
| `/history/[id]` | Full view of a single check-in |

### Key Components

- **AuthProvider** — React context wrapping the app. Holds access token in state, manages refresh logic, provides `user` and `logout` to children.
- **CheckInWizard** — 5-step form with Ark UI Progress bar, back/next navigation. Auto-saves via PUT on each step completion. Steps: wins → friction → energy rating → signal moment → intentions.
- **CheckInCard** — Summary card for the timeline: date, energy rating badge, truncated wins preview. Clickable to expand.
- **CheckInDetail** — Full read-only view of a completed check-in with all 5 fields displayed.
- **Header** — Minimal navigation: logo (left), history link, profile/logout (right).
- **EnergySlider** — Ark UI Slider component styled for 1-10 energy rating with color gradient.

### Ark UI Components Used

- **Slider** → energy rating input (1-10)
- **Progress** → wizard step indicator
- **Dialog** → confirm logout, discard draft
- **Toast** → save confirmations, error messages

### API Client

Thin fetch wrapper in `lib/api.ts`:
- Automatic `Authorization: Bearer` header attachment from AuthProvider
- 401 interception triggers silent token refresh and retry
- Typed request/response interfaces for each endpoint

## Visual Design

Clean and modern aesthetic — white backgrounds, indigo/violet accent color, sharp typography, subtle shadows. Inspired by Linear, Notion, Vercel.

### Design Tokens

- **Primary:** Indigo-600 (`#4F46E5`) — buttons, active states, links
- **Background:** White (`#FFFFFF`) — main surfaces
- **Surface:** Gray-50 (`#F9FAFB`) — cards, secondary areas
- **Border:** Gray-200 (`#E5E7EB`) — dividers, card borders
- **Text primary:** Gray-900 (`#111827`)
- **Text secondary:** Gray-500 (`#6B7280`)
- **Success:** Emerald-500 — completion states
- **Error:** Red-500 — validation errors

### Typography

- **Font:** Inter (or Geist Sans)
- **Headings:** Semibold, tight letter-spacing
- **Body:** Regular weight, comfortable line-height

### Layout Principles

- Mobile-first responsive
- Generous padding and whitespace
- `rounded-lg` corners on cards and inputs
- Subtle `shadow-sm` on elevated surfaces
- Max content width ~640px for the check-in wizard (centered)

## Check-in UX Flow

Step-by-step wizard. One prompt per screen.

1. **Wins** — "What moved forward this week?" — textarea
2. **Friction** — "Where did you feel resistance?" — textarea
3. **Energy Rating** — "How was your energy this week?" — slider 1-10
4. **Signal Moment** — "What interaction or moment is still on your mind?" — textarea
5. **Intentions** — "What matters most next week?" — textarea

Each step:
- Shows a progress bar (step N of 5)
- Has Back and Next buttons (step 1 has no Back, step 5 has "Complete" instead of Next)
- Auto-saves the current field via PUT when the user advances
- Allows going back to edit previous answers

On completion: redirects to the check-in detail view showing all 5 answers.

## History View

Reverse-chronological timeline feed.

Each card shows:
- Week date (e.g., "Week of April 13, 2026")
- Energy rating as a colored badge
- First ~100 characters of wins as a preview
- Completed/incomplete indicator

Click a card to navigate to `/history/[id]` for the full read-only view.

Paginated — loads 10 entries initially, "Load more" at the bottom.

## Landing Page Logic

When authenticated user visits `/check-in`:

1. Fetch `GET /api/check-ins/current`
2. If **404** (no check-in this week) → show the CheckInWizard
3. If **200 + completed: false** → resume the wizard from where they left off
4. If **200 + completed: true** → show the CheckInDetail view with a "View History" link

## Local Development

Docker Compose at the repo root provides:
- **PostgreSQL 15** — `localhost:5432`, database `reflect_dev`
- **Redis 7** — `localhost:6379` (for future use — session store, caching)

Spring Boot runs with `-Dspring-boot.run.profiles=dev`. Next.js runs with `npm run dev` proxying API calls to `localhost:8080`.

## Out of Scope (Deferred)

Explicitly not included in this MVP:
- AI-generated insights (Phase 2)
- Stripe billing / subscriptions (Phase 2)
- Reminder emails (Phase 2)
- MCP server integration (Phase 2)
- AI agent layer (Phase 3)
- Team features (Phase 4)
- OAuth 2.0 / social login (Phase 4)
- Password reset flow (post-MVP)
- Email verification (post-MVP)
