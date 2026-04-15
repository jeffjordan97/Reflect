# ADR-006 — Next.js on Vercel for Frontend

**Status:** Accepted  
**Date:** April 2026  
**Author:** Jeffrey Jordan

---

## Context

Frontend options evaluated:
1. **Next.js on Vercel** — SSR/SSG for SEO, global CDN, preview deployments per PR,
   zero-config HTTPS, tight GitHub integration
2. **React SPA on Vercel/Netlify** — simpler, but no SSR — poor SEO for content pages
   (landing page, blog posts)
3. **Server-rendered HTML from Spring Boot (Thymeleaf)** — single deployment, but
   poor DX for interactive UI (energy slider, real-time charts); no modern React
   component model

## Decision

Use **Next.js 14 (App Router) deployed to Vercel**. The landing page and marketing
content use SSR for search engine crawlability — critical for the content marketing
strategy (SEO targeting "weekly review template" and related queries). The authenticated
app uses client-side rendering within the App Router's layout.

## Consequences

**Positive:**
- SSR for landing page improves Google indexing immediately
- Vercel preview URLs per GitHub PR — design review without local setup
- Zero infrastructure management — Vercel handles CDN, HTTPS, deployment
- Next.js font optimisation (`next/font/google`) for DM Sans + Fraunces — no layout
  shift, served from Vercel's edge network

**Negative / Accepted risks:**
- Splits the stack across two platforms (Railway + Vercel) — mitigated by the fact
  that `NEXT_PUBLIC_API_URL` is a single env var; switching to self-hosted Next.js
  on Railway is a 30-minute change if Vercel pricing becomes a concern
- Next.js App Router has a steeper learning curve than Pages Router — accepted;
  App Router is now the documented default and layouts simplify the authenticated
  vs unauthenticated split
- Vercel free tier limits (100GB bandwidth/mo) are sufficient for Phase 1–2
