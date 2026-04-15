# ADR-003 — API Key Authentication for MCP (with OAuth 2.0 Path)

**Status:** Accepted  
**Date:** April 2026  
**Author:** Jeffrey Jordan

---

## Context

MCP authentication options evaluated:
1. OAuth 2.0 authorisation code flow — industry standard, supports scope granularity,
   time-limited tokens. High implementation complexity (authorisation server, PKCE,
   token endpoints, refresh flow).
2. Personal API key — simple to implement, well-understood, instantly revocable.
   Less secure by default (no expiry unless explicitly implemented).
3. Session cookie sharing — not viable across different domains (Claude.ai ≠ reflect.app)

PRD Open Question Q8 required resolution before the Week 7 MCP implementation sprint.

## Decision

**Launch with personal API key authentication.** Keys are:
- Generated as cryptographically random 32-byte strings, hex-encoded
- Stored as SHA-256 hashes in the `api_keys` table — never in plaintext
- Displayed to the user exactly once at generation
- Scoped to the authenticated user only (no cross-user access possible)
- Revocable immediately via Settings > Integrations

**OAuth 2.0 is deferred to Phase 3** as an enhancement for AI clients that require
the OAuth connector model (e.g. Claude.ai's native MCP connector). API keys remain
available permanently as a power-user alternative.

## Consequences

**Positive:**
- Week 11 MCP launch is achievable — API key implementation is 1–2 days of engineering
- Well-understood by the target audience (developers)
- Instantly revocable — better security posture than session-based approaches

**Negative / Accepted risks:**
- API keys have no built-in expiry — mitigated by: (a) revocation is immediate,
  (b) rotation guidance documented in Settings UI ("rotate every 90 days")
- OAuth provides finer scope control — not needed until third-party client integrations
  require delegated access beyond the current 4-tool scope
- Some AI client platforms may eventually require OAuth — this is the Phase 3 trigger
