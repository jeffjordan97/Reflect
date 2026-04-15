# ADR-004 — SSE Transport for MCP

**Status:** Accepted  
**Date:** April 2026  
**Author:** Jeffrey Jordan

---

## Context

MCP specification v1.0 supports two transports:
1. **SSE (Server-Sent Events)** — HTTP-based, stateless per tool call, works through
   standard proxies and load balancers, no persistent connection state
2. **WebSocket** — persistent bidirectional connection, lower per-message overhead,
   requires connection state management and sticky sessions or a shared connection store

PRD Open Question Q6 required resolution before implementation.

## Decision

Use **SSE transport** for MCP v1.0. Spring Boot has first-class SSE support via
`SseEmitter`. SSE is stateless per tool call, which aligns with the monolith
architecture and Railway's load balancing model.

## Consequences

**Positive:**
- Spring MCP SDK (where available) defaults to SSE — minimal custom implementation
- Works through Railway's load balancer without sticky sessions
- Stateless — no connection state to manage or recover after restarts
- Simpler to test — each tool call is an independent HTTP request

**Negative / Accepted risks:**
- Slightly higher overhead per call vs WebSocket (~50ms per round trip)
  — at Reflect's launch scale this is negligible
- SSE is unidirectional (server → client) — for MCP's request/response pattern
  this is implemented as POST request + SSE response stream
- If real-time streaming features are added (Phase 4), WebSocket transport
  will be evaluated. This requires no breaking change to the MCP tool schema —
  only the transport layer changes
