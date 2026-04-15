# ADR-001 — Monolith over Microservices at Launch

**Status:** Accepted  
**Date:** April 2026  
**Author:** Jeffrey Jordan

---

## Context

Reflect is a solo-founder product with 4–8 hours of development time available per week.
Microservices impose significant operational overhead: service discovery, distributed
tracing, inter-service authentication, independent deployment pipelines, and multiple
failure domains to monitor.

At the anticipated scale of Phase 1–2 (< 5,000 users), no single service will experience
load that a single Railway container cannot handle through vertical scaling.

## Decision

Ship a single Spring Boot JAR containing all business logic (auth, check-in, insight,
billing, MCP, agent layer). Internal boundaries are enforced through package structure and
service interfaces, not deployment boundaries.

Services are extracted to separate deployments only when a specific, measured scaling
bottleneck justifies the operational overhead.

## Consequences

**Positive:**
- Simple deployment — one Railway service, one Neon database, one Upstash Redis
- Unified logging and tracing — no distributed trace correlation required
- No network latency between service calls
- Easier local development — one `docker-compose up`
- Debugging is straightforward — full stack in one process

**Negative / Accepted risks:**
- Single point of failure — mitigated by Railway's auto-restart and zero-downtime deploy
- Cannot scale individual services independently — at < 10k users, vertical scaling
  (upgrading Railway plan) is the correct and cheaper response
- Tight coupling risk — mitigated by strict package-level boundaries and no
  cross-layer direct calls

## Review trigger

Revisit this decision when any of the following occur:
- A single service (e.g. InsightSynthesisAgent) consumes > 60% of container resources
  while others are idle
- Railway Pro plan is insufficient and the cost of a dedicated deployment per service
  is lower than vertical scaling
- Team grows beyond 2 engineers and deployment coordination becomes a bottleneck
