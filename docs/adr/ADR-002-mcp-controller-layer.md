# ADR-002 — MCP Adapter as Controller Layer, Not Separate Service

**Status:** Accepted  
**Date:** April 2026  
**Author:** Jeffrey Jordan

---

## Context

The MCP server needs access to the same service layer (CheckInService, InsightService)
as the REST API. Options considered:

1. Separate Spring Boot service for MCP — requires inter-service HTTP calls or shared DB access
2. MCP as a controller package within the existing monolith — shares the service layer directly
3. Standalone MCP proxy — translates MCP calls to REST API calls

## Decision

Implement the MCP adapter as an additional Spring Boot controller package (`com.reflect.mcp`)
within the same application. MCP tool calls authenticate via a dedicated `McpAuthFilter` and
delegate immediately to the service layer. No business logic lives in the MCP package.

## Consequences

**Positive:**
- No code duplication — MCP tools and REST endpoints share the same service implementations
- Unified authentication filter chain via Spring Security
- Single deployment — no additional Railway service required
- Simpler testing — MCP handlers tested with MockMvc alongside REST controllers

**Negative / Accepted risks:**
- MCP and REST share the same thread pool — long-lived SSE connections could pressure
  REST throughput at scale. Mitigated by a dedicated async thread pool for MCP SSE
  in `AsyncConfig.java` (core: 10, max: 50, queue: 100)
- If MCP adoption is very high, extracting to a separate Railway service via path-based
  routing (/mcp/* → dedicated service) is the escape hatch — no code change required,
  only infrastructure change
