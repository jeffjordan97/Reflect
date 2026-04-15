# ADR-005 — Async Batch Processing for AI Insight Generation

**Status:** Accepted  
**Date:** April 2026  
**Author:** Jeffrey Jordan

---

## Context

Claude API calls for monthly insight generation take 5–30 seconds per user. At 750 Pro
users, the total batch takes 60–370 minutes if run sequentially.

Options evaluated:
1. **Synchronous on-demand** — user triggers insight generation; waits for Claude response.
   Unacceptable: 5–30 second blocking request violates the NFR (no user-blocking wait).
2. **Async batch (scheduled)** — insights generated overnight on the 1st of each month.
   User sees cached result next time they open the app.
3. **Streaming** — insights generated in real time and streamed to the client.
   High complexity; Claude API streaming requires persistent connection management.

## Decision

**Async batch processing** via Spring `@Scheduled`. InsightSynthesisAgent runs at
02:00 UTC on the 1st of each month. Results stored in `insight_reports` and cached
in Upstash Redis (30-day TTL). All MCP `get_insights` calls are served from cache.

A Redis lock prevents double-generation if the job is triggered twice (e.g. Railway
restart during job execution).

## Consequences

**Positive:**
- Zero user-facing latency — insights are ready when the user opens the app
- Controllable API cost — one batch per month, sequential with 1s sleep between users
- Resilient — failed users are logged and can be retried manually
- Redis cache means even high MCP traffic generates zero additional Claude API calls

**Negative / Accepted risks:**
- Insight delivery is delayed up to 24 hours after month-end — accepted per
  PRD Assumption: "users tolerate a 1-day delay"
- Long-running batch at scale (750 users × 10s = 2hr batch) — mitigated by:
  (a) sequential processing with sleep respects Anthropic rate limits,
  (b) Phase 4 option to extract to a separate Railway service or background queue
- If a user joins mid-month, they receive their first insight at the next month-end
  — a known limitation, documented in the onboarding flow
