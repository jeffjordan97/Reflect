# Reflect — Product Requirements Document

| Field | Value |
|-------|-------|
| Document Version | **1.2 — Agent Architecture & Prompt Redesign** |
| Status | Draft |
| Product | Reflect — Guided Weekly Review & AI Insight Platform |
| Author | Jeffrey Jordan |
| Date | April 2026 |
| Reviewers | Engineering Lead, Design Lead (TBD) |

**Change log**

| Version | Date | Author | Summary |
|---------|------|--------|---------|
| 1.0 | Apr 2026 | Jeffrey Jordan | Initial draft — all sections |
| 1.1 | Apr 2026 | Jeffrey Jordan | MCP integration added as first-class feature: new persona, Epic 8 (4 user stories + tool schema), NFRs, Flow 4, dependencies, risks, timeline milestones |
| 1.2 | Apr 2026 | Jeffrey Jordan | Agent architecture added (Epic 9): PromptAdaptationAgent, PatternSentinelAgent, NudgeAgent, InsightSynthesisAgent. Prompt redesign (Section 3.4): `blockers → friction`, `energy_notes → signal_moment`. Data model updated. Q9 and Q10 added. |

> **How to use this document**
>
> This PRD is the single source of truth for what Reflect is, who it serves, and what the product must do. It is a living document — sections marked \[TBD\] will be resolved prior to Phase 1 engineering kickoff. User Stories follow the Given/When/Then acceptance criteria format. Feature priority uses the RICE framework (Reach × Impact × Confidence ÷ Effort). Non-functional requirements reference ISO/IEC 25010 quality attributes.

---

## 1. Product Overview

### 1.1 Problem Statement

Working professionals — engineers, product managers, freelancers, and knowledge workers — recognise that consistent self-reflection is a cornerstone of sustained performance and wellbeing. Yet the majority do not have a structured, habitual practice: journals are abandoned after weeks, ad-hoc notes lack analytical depth, and retrospective rituals from professional settings (sprint retros, 1:1s) do not translate to personal life.

The core problem is threefold: there is no structured prompt system that removes blank-page friction; existing journaling tools are unstructured and generate no analytical value over time; and no consumer product builds a longitudinal personal model that grows more valuable the longer a user engages with it.

A secondary problem exists for the most technically advanced segment of this audience: AI-native professionals — engineers and power users who spend significant working time inside AI chat interfaces — face an additional friction layer when reflection requires switching to a separate dedicated app. For this segment, the ideal check-in happens inside the tool they are already using.

> **Problem in one sentence:** Professionals want to improve through reflection but lack the structure, consistency, and feedback loop to make it work — and the most AI-native users should not need to leave their AI tools to do it.

### 1.2 Product Vision

Reflect is a guided weekly review application for working professionals, accessible via a web application and via MCP-compatible AI clients. Users complete a structured 10-minute check-in each Sunday — in the web app or conversationally through Claude, Cursor, or any MCP-enabled tool. An AI engine analyses accumulated review history to surface personalised, longitudinal pattern insights. The longer a user engages, the more intelligent and irreplaceable the product becomes.

### 1.3 Goals & Objectives

| Goal | Objective | Success Metric | Timeframe |
|------|-----------|---------------|-----------|
| Establish habit | Users complete a check-in at least 3 Sundays in 4 | WAU ≥ 70% of Pro base | Month 6 |
| Drive conversion | Free users convert to Pro within 30 days | Free-to-Pro conversion ≥ 10% | Month 4 |
| Reduce churn | Pro users remain active long-term | Monthly churn < 4% | Month 6 |
| Deliver AI value | Insights rated useful by majority of Pro users | AI insight rating ≥ 4/5 | Month 5 |
| **\[MCP\]** AI-native adoption | Pro users connect Reflect to an AI client | MCP OAuth connections ≥ 20% of Pro base by Month 6 | Month 6 |
| Revenue milestone | Sustainable side income from subscriptions | MRR ≥ £3,742 by Month 12 | Month 12 |

### 1.4 Scope Summary

| In Scope (v1.0 + v1.1 + v1.2) | Out of Scope |
|-------------------------------|--------------|
| Web application (desktop + mobile browser) | Native iOS / Android app |
| Weekly check-in with 5 structured prompts (v1.2 schema) | Custom prompt builder |
| Entry history, trend charts, streaks | Third-party calendar integrations (Jira, Linear, Notion) |
| AI monthly & quarterly pattern insights (Claude API) | Real-time AI chat within the web app |
| Email reminders and monthly digest | SMS or push notifications |
| Stripe-powered subscription billing (Monthly + Annual) | Team / multi-user plans (Phase 4) |
| Year-in-Review shareable card | White-label / enterprise features |
| **\[MCP\]** Reflect MCP server (Pro feature) | MCP access on free tier |
| **\[MCP\]** OAuth 2.0 account linking for AI clients | Custom MCP client development |
| **\[MCP\]** 4 MCP tools: submit_checkin, get_insights, get_history, get_streak | Webhook / event streaming via MCP |
| **\[Agent\]** PromptAdaptationAgent (Phase 3) | Agent layer on free tier |
| **\[Agent\]** PatternSentinelAgent (Phase 3) | Multi-user / team agent features |
| **\[Agent\]** NudgeAgent (Phase 3) | |
| **\[Agent\]** InsightSynthesisAgent (Phase 3) | |

---

## 2. User Personas

### 2.1 Primary Persona — The Reflective Engineer

| Attribute | Detail |
|-----------|--------|
| Name | Alex, 29 |
| Role | Senior Software Engineer at a mid-size SaaS company |
| Goal | Level up toward a Staff/Lead role; understand personal performance patterns |
| Frustration | Sprint retros exist at work, but nothing exists for personal growth outside of it |
| Behaviour | Uses Notion for notes but inconsistently; has tried journaling twice and quit |
| Motivation | Intrinsic — wants to feel in control of career trajectory |
| Willingness to pay | £4.99–9.99/mo without hesitation if value is clear |
| Discovery | Twitter/X, Hacker News, developer newsletters |
| **MCP relevance** | **High — uses Claude or Cursor daily; will connect Reflect via MCP once available** |

### 2.2 Secondary Persona — The Freelance Consultant

| Attribute | Detail |
|-----------|--------|
| Name | Maya, 34 |
| Role | Independent UX/Product Consultant, 3–4 clients at any time |
| Goal | Track output week-to-week; protect against burnout; stay accountable without a manager |
| Frustration | No system connects weekly output to how she actually felt — decisions become reactive |
| Behaviour | Tracks billable hours in Toggl; uses voice memos for reflections that she never reviews |
| Motivation | Extrinsic accountability + data — wants receipts for her own performance |
| Willingness to pay | £39.99/yr annual plan preferred — predictable cost aligns with project income |
| Discovery | Productivity newsletters, LinkedIn, referral from peer consultant |
| **MCP relevance** | Low-Medium — uses AI tools but prefers the web app for structured input |

### 2.3 Tertiary Persona — The Engineering Manager

| Attribute | Detail |
|-----------|--------|
| Name | Dan, 38 |
| Role | Engineering Manager, 6 direct reports |
| Goal | Model reflective practice for the team; improve 1:1 quality with structured personal data |
| Frustration | Team retros exist but individual reflection is inconsistent across reports |
| Behaviour | Uses Range or Geekbot for async check-ins at team level; nothing personal |
| Motivation | Leadership identity — wants to be the kind of manager who invests in self-awareness |
| Willingness to pay | £9.99/seat if a team Reflect feature is available (Phase 4 trigger) |
| Discovery | Engineering leadership Slack communities, TLDR newsletter, Product Hunt |
| **MCP relevance** | Medium — may use MCP to query team insights once Teams plan ships |

### 2.4 New Persona — The AI-Native Developer \[MCP\]

> New in v1.1 — this persona is the primary driver of the MCP integration decision.

| Attribute | Detail |
|-----------|--------|
| Name | Priya, 27 |
| Role | Full-Stack Developer / indie hacker; works primarily inside Cursor + Claude |
| Goal | Build habits that compound without adding apps to her stack |
| Frustration | Every productivity tool requires a context switch — she lives in her AI interface and resents leaving it |
| Behaviour | Has Claude open 6+ hours a day; uses MCP tools for Notion, GitHub, and calendar; runs her entire workflow through AI-mediated actions |
| Motivation | Efficiency maximalism — if it cannot integrate into her AI environment, she will not use it |
| Willingness to pay | £4.99/mo immediately if MCP integration is available; would not sign up for a web-only tool |
| Discovery | MCP tool directories, Cursor marketplace, Twitter/X build-in-public threads |
| **MCP relevance** | **Critical — MCP is the primary acquisition and retention surface for this persona. Web app is secondary.** |

---

## 3. User Stories & Functional Requirements

### 3.1 Prioritisation Framework

Features are prioritised using the RICE framework: Reach × Impact × Confidence ÷ Effort.

| Tier | Label | Definition |
|------|-------|------------|
| **P0** | Must Have | Launch blocker. Product cannot ship without this. |
| **P1** | Should Have | High value; targeted for Phase 1–2. Shipping without degrades product significantly. |
| **P2** | Nice to Have | Phase 3+. Adds differentiation but not launch-critical. |
| **P3** | Future | Icebox. Valid ideas to revisit at scale. |

---

### 3.2 Epic 1: Onboarding & Authentication

#### US-01 — New visitor account creation

**As a** new visitor, **I want to** create an account and understand the product's value before signing up, **so that** I know what I'm committing to and can begin my first check-in immediately.

**Priority:** P0 — Must Have | **RICE:** ~580

**Acceptance Criteria:**
- Given I land on the homepage, When I click 'Get Started', Then I see a 3-step value proposition before the sign-up form
- Given I complete the sign-up form with a valid email and password, When I submit, Then I receive a verification email within 60 seconds
- Given I click the verification link, When I am authenticated, Then I am redirected to the onboarding welcome screen
- Given I am on the welcome screen, When I click 'Start My First Check-In', Then the week's check-in form opens immediately

---

#### US-02 — Returning user login

**As a** returning user, **I want to** log in securely and resume from where I left off, **so that** I can continue my practice without friction.

**Priority:** P0 — Must Have | **RICE:** ~520

**Acceptance Criteria:**
- Given I am on the login page, When I enter valid credentials and submit, Then I am authenticated and redirected to my dashboard within 2 seconds
- Given I enter an incorrect password 5 times, When the 5th attempt fails, Then my account is temporarily locked for 15 minutes and I receive an email alert
- Given I click 'Forgot password', When I submit my email, Then I receive a reset link within 60 seconds valid for 24 hours

---

### 3.3 Epic 2: Weekly Check-In

#### US-03 — Complete weekly check-in

**As a** Pro user, **I want to** complete a structured weekly check-in with guided prompts each Sunday, **so that** I build a consistent reflection habit without staring at a blank page.

**Priority:** P0 — Must Have | **RICE:** ~720

**Acceptance Criteria:**
- Given it is Sunday and I open the app, When I navigate to 'This Week', Then I see the check-in form with 5 prompts across 3 sections: Reflection (2), Energy (1 rated + 1 text), Intentions (1)
- Given I complete all 5 prompts, When I click 'Complete Check-In', Then my entry is saved, my streak increments, and I see a confirmation screen
- Given I have already submitted this week's entry, When I revisit, Then I see a read-only view with an 'Edit' option until Monday 23:59
- Given I begin a check-in but do not submit, When I close and return, Then my draft is preserved

---

#### US-04 — Free-tier check-in limit

**As a** free-tier user, **I want to** complete up to 4 check-ins before being prompted to upgrade, **so that** I can evaluate the product before committing.

**Priority:** P0 — Must Have | **RICE:** ~610

**Acceptance Criteria:**
- Given I am a free-tier user who completes my 4th check-in, When I submit, Then I see a dismissible upgrade prompt on the confirmation screen
- Given I attempt a 5th check-in as a free user, When I open the form, Then I see a paywall screen with Stripe checkout link
- Given I click 'Maybe Later', Then I return to my dashboard with a persistent non-intrusive banner

---

### 3.4 Check-In Prompt Design (Revised v1.2)

> **v1.2 Change — Prompt Redesign (Gap Resolution)**
>
> Research into the AI response gap identified that the original 5 prompts were output-oriented (wins, blockers, intentions) but lacked the interpersonal and self-awareness dimension that gives reflection its depth. A revised prompt set is adopted below, informed by positive psychology research (Carden et al., 2022) and the LLM consensus answer that originally defined the product need. The data schema is updated to match (`signal_moment` field added to CheckIn entity). The energy slider and intentions prompt are retained; three prompts are refined.

| # | Field Name | Prompt Label | Placeholder / Guidance | Section | Change from v1.1 |
|---|-----------|--------------|------------------------|---------|-----------------|
| 1 | `wins` | Progress | What moved forward this week — in your work or in how you worked with others? | Reflection | Refined: 'Progress' broadens from output wins to include collaboration and interpersonal forward movement |
| 2 | `friction` | Friction | Where did you feel resistance this week — a task, a person, or yourself? | Reflection | Renamed from 'Blockers'. 'Friction' captures avoidance behaviours and interpersonal difficulty, not only external blockers |
| 3 | `energy_rating` | Energy | How was your energy? Slide to rate (1 = very low, 10 = excellent) | Energy | Unchanged — slider mechanic retained |
| 4 | `signal_moment` | Signal moment | What interaction or moment from this week is still on your mind — and why? | Energy / Awareness | **NEW prompt.** Surfaces the interpersonal and emotional dimension the LLM response highlighted |
| 5 | `intentions` | Next week | What matters most to you next week — and why does it matter? | Intentions | Refined: "and why does it matter?" adds one layer of self-awareness without adding friction |

**Prompt design principles applied:**

| Principle | Application |
|-----------|------------|
| Direct | Short labels (Progress, Friction, Signal moment) remove cognitive load |
| Warm | Second-person voice, no clinical language ('friction' not 'impediments') |
| Honest | The signal_moment prompt invites discomfort — 'still on your mind' acknowledges it without forcing disclosure |
| Interpersonal | Progress prompt explicitly includes 'how you worked with others'; signal_moment is inherently relational |
| AI-parseable | All 5 fields remain discrete text or integer — no change to the data schema structure, only field rename (blockers → friction) and addition (signal_moment) |

---

### 3.5 Epic 4: AI Insight Engine

#### US-06 — Monthly AI pattern insight

**As a** Pro user, **I want to** receive a personalised AI-generated monthly pattern insight report, **so that** I understand patterns in my working life that I could not see on my own.

**Priority:** P1 — Should Have | **RICE:** ~680

**Acceptance Criteria:**
- Given I have completed at least 4 check-ins in a calendar month, When the 1st of the next month arrives, Then an AI report is generated and available in-app and via email
- Given the report is available, When I open it, Then I see: a 3-sentence month summary, 2–3 named patterns, interpersonal themes (from signal_moment data), and one recommended focus
- Given I rate the report with thumbs up/down, Then my rating is stored and used to improve future generation quality

---

#### US-07 — Quarterly deep review

**As a** Pro user, **I want to** receive an AI-generated quarterly deep review, **so that** I gain a higher-level perspective on my quarter that I can act on.

**Priority:** P1 — Should Have | **RICE:** ~480

**Acceptance Criteria:**
- Given I have at least 10 check-ins in the past 13 weeks, When the first Sunday of a new quarter arrives, Then a quarterly review is generated
- Given the quarterly review is available, When I click 'Set as Goal', Then the recommended goal pre-populates my next check-in's Intentions field

---

### 3.6 Epic 5: Notifications & Reminders

#### US-08 — Sunday reminder email

**As a** any user, **I want to** receive a Sunday morning email reminder to complete my check-in, **so that** I maintain my streak without relying on memory.

**Priority:** P0 — Must Have | **RICE:** ~540

**Acceptance Criteria:**
- Given I have not submitted this week's check-in, When it is Sunday at 09:00 in my local timezone, Then I receive a reminder email with a direct check-in link
- Given I have already submitted, When Sunday 09:00 arrives, Then I do not receive a reminder
- Given I want to adjust reminder time or opt out, When I visit Settings > Notifications, Then I can select a time or disable the reminder

---

### 3.7 Epic 6: Subscription & Billing

#### US-09 — Upgrade to Pro

**As a** free-tier user, **I want to** upgrade to Pro using a simple, trusted checkout flow, **so that** I can access the full product without friction or payment anxiety.

**Priority:** P0 — Must Have | **RICE:** ~630

**Acceptance Criteria:**
- Given I click any upgrade CTA, When Stripe Checkout opens, Then I see a clear plan summary (£4.99/mo or £39.99/yr) before entering card details
- Given I complete checkout, When payment succeeds, Then my account is upgraded to Pro within 5 seconds
- Given my payment fails, When Stripe sends a failure webhook, Then I receive an email within 5 minutes and retain access for a 7-day grace period
- Given I cancel via the Stripe portal, When the billing period ends, Then my account downgrades to free tier

---

### 3.8 Epic 7: Growth Features (Phase 3)

#### US-10 — Year-in-Review shareable card

**As a** Pro user, **I want to** generate and share my Year-in-Review card, **so that** I celebrate my progress and introduce Reflect to my network organically.

**Priority:** P2 — Nice to Have | **RICE:** ~310

**Acceptance Criteria:**
- Given I have at least 12 entries in the calendar year, When I navigate to 'Year in Review', Then I can preview my personalised summary card
- Given I click 'Share', When the share sheet opens, Then I see Twitter/X, LinkedIn, and PNG download options
- Given my card is shared, Then it includes a 'Made with Reflect' attribution link

---

#### US-11 — Accountability partner

**As a** Pro user, **I want to** invite an accountability partner to receive my weekly summary, **so that** I have external accountability that reinforces my habit.

**Priority:** P2 — Nice to Have | **RICE:** ~260

**Acceptance Criteria:**
- Given I enter a partner's email and click 'Invite', When they opt in, Then they receive a brief summary (wins + intentions only) after each of my check-ins
- Given my partner opts out at any time, Then they are immediately removed and I am notified

---

### 3.9 Epic 8: MCP Integration \[Phase 2 — Pro Feature\]

> **What is the Reflect MCP Server?**
>
> Reflect exposes a Model Context Protocol (MCP) server that allows Pro users to interact with their check-in data directly inside MCP-compatible AI clients such as Claude.ai, Cursor, and Windsurf. The MCP server is a thin adapter layer that sits in front of the existing Spring Boot REST API. Authentication is handled via personal API keys (OAuth 2.0 deferred to Phase 3 per ADR-003). MCP access is a Pro-only feature.

#### MCP Tool Schema

| Tool Name | Description | Parameters | Returns |
|-----------|-------------|-----------|---------|
| `submit_checkin` | Submit the current week's check-in. Idempotent — calling again before Monday 23:59 overwrites the draft. | `wins: string`, `friction: string`, `energy_rating: integer (1–10)`, `signal_moment: string`, `intentions: string` | `{ success: bool, streak: int, week: string }` |
| `get_insights` | Retrieve the most recent AI-generated insight report. | `period: enum('monthly'|'quarterly')`, `n: integer (default 1, max 4)` | `{ period, summary, patterns, interpersonal_themes, recommendation, generated_at }` |
| `get_history` | Retrieve recent check-in entries as structured data. | `n: integer (default 4, max 12)` | Array of `{ week_start, wins, friction, energy_rating, signal_moment, intentions }` |
| `get_streak` | Retrieve the current check-in streak and last submission date. | None | `{ streak: int, last_checkin: ISO8601, next_due: ISO8601 }` |

**Example AI Conversation — Check-In via Claude:**

> **User (inside Claude.ai):** "Log this week's check-in. Progress: shipped the auth module, unblocked backend team. Friction: still waiting on design sign-off for the dashboard. Energy 7 — solid week but meetings were heavy. Signal moment: had a useful 1:1 with my manager where I finally asked directly about proactive communication — turned out to be simpler than I thought. Intentions: get the dashboard PR reviewed."
>
> **Claude (calls submit_checkin tool):** "Done. Your Week 14 check-in is saved. Streak is now 14 weeks. Based on last month's pattern, heavy meeting weeks tend to drop your energy rating — you might want to protect one morning block next week."

---

#### US-12 — Link Reflect account to AI client \[MCP\]

**As a** Pro user using an MCP-compatible AI client, **I want to** link my Reflect account to my AI client via API key so that tools are authenticated to my account, **so that** I can use Reflect tools inside Claude or Cursor without sharing my password.

**Priority:** P1 — Should Have | **RICE:** ~580

**Acceptance Criteria:**
- Given I navigate to Settings > Integrations in the Reflect web app, When I click 'Generate API Key', Then I see the key displayed once with a copy button
- Given I add the Reflect MCP server URL to my AI client's MCP configuration, When I enter my API key, Then tool calls are authenticated as my account
- Given my API key is active, When I call any Reflect MCP tool, Then the response reflects my personal data
- Given I click 'Revoke' in Settings > Integrations, When confirmed, Then the key is immediately invalidated and tool calls return 401

---

#### US-13 — Submit check-in conversationally \[MCP\]

**As a** Pro user inside an AI chat, **I want to** submit my weekly check-in conversationally without opening the Reflect web app, **so that** I can maintain my reflection habit from within my primary working environment.

**Priority:** P1 — Should Have | **RICE:** ~640

**Acceptance Criteria:**
- Given I am authenticated via API key, When I describe my week to my AI assistant and ask it to log my check-in, Then the AI calls submit_checkin with the extracted parameters and confirms the save
- Given the check-in is submitted, When I ask the AI for confirmation, Then it responds with my updated streak and optionally surfaces a relevant insight
- Given I am a free-tier user attempting to use submit_checkin, When the tool is called, Then the API returns a 403 with an upgrade message that the AI surfaces conversationally
- Given I call submit_checkin more than once in a week, When the second call arrives, Then it overwrites the draft (idempotent) and confirms with 'Check-in updated'

---

#### US-14 — Query insights conversationally \[MCP\]

**As a** Pro user inside an AI chat, **I want to** ask my AI assistant to summarise my recent Reflect insights and check-in history, **so that** I can query my personal data contextually without navigating to the web app.

**Priority:** P1 — Should Have | **RICE:** ~520

**Acceptance Criteria:**
- Given I ask 'What patterns has Reflect spotted for me this month?', When the AI calls get_insights, Then it receives the latest monthly report and summarises it conversationally
- Given I ask 'How has my energy been the last 4 weeks?', When the AI calls get_history(n=4), Then it produces a readable trend summary
- Given no insight report has been generated yet, When get_insights is called, Then the API returns a 404 with a message: 'No insight report yet — complete 4 check-ins to unlock'

---

#### US-15 — API-level entitlement enforcement \[MCP\]

**As a** Pro user, **I want to** have all entitlement rules enforced at the API level regardless of which client I use, **so that** the same fair-usage limits apply whether I use the web app or an AI client.

**Priority:** P1 — Should Have | **RICE:** ~460

**Acceptance Criteria:**
- Given a free-tier user calls any Reflect MCP tool, When the API processes the request, Then it returns HTTP 403 with `{ "error": "PRO_REQUIRED", "upgrade_url": "..." }`
- Given a rate limit of 60 tool calls per hour per user is exceeded, When the next call arrives, Then the API returns 429 with Retry-After header

---

### 3.10 Epic 9: Agentic AI Layer \[Phase 3 — Pro Feature\]

> **What is the Reflect Agent Layer?**
>
> The Agent Layer upgrades Reflect's InsightService from a single-shot monthly batch processor to a proactive, longitudinal, multi-specialist system. Four specialist agents are coordinated by an AgentOrchestrator implemented as Spring `@Scheduled` + Spring Events — not a separate service (consistent with ADR-001).

**The four specialist agents:**

| Agent | Responsibility | Trigger | Output |
|-------|---------------|---------|--------|
| `PromptAdaptationAgent` | Analyses the last 4 check-ins to detect which prompts the user is writing least about. Generates a contextual follow-up suggestion for the upcoming Sunday reminder. | Thursday before each Sunday (`@Scheduled`) | Optional contextual addition to Sunday reminder email |
| `PatternSentinelAgent` | Runs after every check-in submission. Compares the new entry against historical data looking for pattern emergence or anomaly. Writes detected signals to the `pattern_signals` table. | Post check-in submission (Spring `ApplicationEvent`) | `pattern_signals` rows; triggers NudgeAgent if `signal_strength > threshold` |
| `NudgeAgent` | Consumes pattern signals from PatternSentinelAgent. Decides whether to surface a mid-week nudge. Applies rate limiting — maximum 1 nudge per user per week. | Triggered by PatternSentinelAgent signal; respects 7-day cooldown | In-app notification or email: specific, not generic |
| `InsightSynthesisAgent` | Replaces the existing batch InsightGenerationJob. Produces monthly and quarterly reports using a richer prompt that includes `signal_moment` data and `pattern_signals` history. | 1st of month, 02:00 UTC (`@Scheduled`) | `InsightReport` with enriched content including `interpersonal_themes` |

---

#### US-16 — Adaptive Sunday prompt \[Agent\]

**As a** Pro user, **I want to** receive a contextual prompt adaptation in my Sunday reminder based on what I've been avoiding in recent check-ins, **so that** my reflection goes deeper than it would with a generic prompt.

**Priority:** P2 — Nice to Have | **RICE:** ~380

**Acceptance Criteria:**
- Given the PromptAdaptationAgent has detected I have written fewer than 50 words on 'signal_moment' for the last 3 weeks, When Thursday arrives, Then my Sunday reminder includes an additional line contextualising the prompt
- Given the adaptation is generated, When it is included in the email, Then it appears below the standard reminder copy as an optional suggestion, not a mandatory instruction
- Given I have not shown a pattern of avoidance, When Thursday arrives, Then no adaptation is appended — the reminder is standard
- Given I opt out of adaptive prompts in Settings > Notifications, When Thursday arrives, Then no adaptation is ever generated for my account

---

#### US-17 — Proactive mid-week nudge \[Agent\]

**As a** Pro user, **I want to** receive a proactive mid-week nudge when the AI detects a significant pattern emerging in my check-ins, **so that** I can act on a pattern while it is still current, not after a month has passed.

**Priority:** P2 — Nice to Have | **RICE:** ~440

**Acceptance Criteria:**
- Given PatternSentinelAgent detects I have listed the same friction for 3+ consecutive weeks, When the signal_strength threshold is exceeded, Then NudgeAgent generates a nudge within 24 hours of the triggering check-in
- Given a nudge is generated, When it is delivered, Then it is specific (names the actual pattern) not generic ('You have a recurring blocker')
- Given a nudge was delivered in the last 7 days, When PatternSentinelAgent detects another signal, Then no second nudge is generated — the 7-day cooldown is enforced
- Given I am a free-tier user, When PatternSentinelAgent runs, Then no nudge is generated — nudges are Pro-only

---

## 4. Non-Functional Requirements

Non-functional requirements use the ISO/IEC 25010 quality attribute framework. MCP-specific NFRs are marked \[MCP\]; agent-specific NFRs are marked \[Agent\].

| ISO 25010 Attribute | Requirement | Metric / Target |
|--------------------|-------------|----------------|
| Performance Efficiency | API response time for dashboard load (P95) | < 500ms at 2× expected concurrent load |
| Performance Efficiency | Check-in form submit response time | < 300ms acknowledgement |
| Performance Efficiency | AI insight generation (async background job) | Complete within 5 minutes of trigger; no user-blocking wait |
| Performance Efficiency | **\[MCP\]** MCP tool call end-to-end latency (P95) | < 800ms from tool invocation to API response |
| Performance Efficiency | **\[Agent\]** PatternSentinelAgent post-submission run time | Complete within 2 seconds (async @EventListener) |
| Reliability | API availability SLO | 99.9% monthly uptime (~43 min/mo max downtime) |
| Reliability | Stripe webhook processing | Idempotent; all events processed within 30 seconds |
| Reliability | **\[MCP\]** API key validation | Redis-cached (24hr TTL); DB fallback on cache miss |
| Security | All user PII encrypted at rest | AES-256 via Neon encryption (managed) |
| Security | All data in transit encrypted | TLS 1.3 minimum |
| Security | Authentication brute-force protection | Account lock after 5 failed attempts; 15-minute lockout |
| Security | **\[MCP\]** API key storage | SHA-256 hashed; displayed once on generation; never stored in plaintext |
| Security | **\[MCP\]** API key scope | Scoped to checkin:write, insights:read, history:read, streak:read only |
| Usability | WCAG 2.1 AA compliance for all web UI | Audited pre-launch using axe DevTools |
| Usability | Check-in form completion time | ≤ 10 minutes first-time; ≤ 6 minutes returning user |
| Usability | **\[MCP\]** Tool descriptions must be LLM-readable | All tool descriptions pass review: clear parameter types, enumerated values, explicit error return shapes |
| Compatibility | Browser support | Chrome 110+, Firefox 110+, Safari 16+, Edge 110+ |
| Compatibility | **\[MCP\]** MCP protocol version | Implement MCP specification v1.0 |
| Maintainability | Test coverage — Spring Boot API service layer | ≥ 80% unit + integration test coverage |
| Maintainability | **\[MCP\]** MCP adapter layer test coverage | ≥ 90% unit test coverage on MCP tool handlers |
| Maintainability | **\[Agent\]** Agent layer test coverage | ≥ 80% unit test coverage; rule-based detection logic fully unit tested |
| Functional Suitability | GDPR data export | User can export all entries as JSON or CSV from Settings |
| Functional Suitability | Right to erasure (GDPR Article 17) | Account deletion removes all PII and entries within 30 days; all API keys revoked immediately |

---

## 5. UX Requirements & Key User Flows

### 5.1 Design Principles

- **Minimal friction** — every interaction should require the least possible cognitive load
- **Ritual-first** — the Sunday check-in is a sacred act; the UI must feel calm, intentional, and focused
- **Progressive disclosure** — free users see what Pro looks like without being overwhelmed by paywalls
- **Mobile-first responsive** — 40%+ of check-ins are expected to occur on mobile browsers
- **WCAG 2.1 AA** — accessibility is a launch requirement, not a post-launch task
- **\[MCP\]** API-first design — every user action available in the web app must have a corresponding API endpoint; MCP tools are thin wrappers, not special-case code paths

### 5.2 Flow 1: First-Time User Activation

| Step | User Action | System Response | Success Criteria |
|------|-------------|----------------|-----------------|
| 1 | Lands on homepage | Value proposition + social proof above fold | Bounce rate < 65% |
| 2 | Clicks 'Get Started' | 3-screen value walkthrough | Walkthrough completion > 70% |
| 3 | Signs up | Account created; verification email sent | Sign-up completion > 60% of walkthrough completions |
| 4 | Verifies email | Redirected to welcome screen | Email verification > 80% |
| 5 | Completes first check-in | Confirmation + streak starts at 1 | First check-in completion > 55% of verified accounts |
| 6 | Views dashboard | Entry appears; upgrade prompt visible (non-intrusive) | Dashboard visits > 80% of first check-in completions |

### 5.3 Flow 2: Weekly Check-In (Returning Pro User — Web App)

| Step | User Action | System Response | UX Note |
|------|-------------|----------------|---------|
| 1 | Receives Sunday reminder email | Email with streak count + direct link | Subject line includes streak: 'Week 12 — keep it going' |
| 2 | Clicks email CTA | Authenticated directly to check-in form | Minimise steps; no extra dashboard redirect |
| 3 | Completes 5 prompts | Auto-save on each field blur | Energy slider is the most distinctive UI element |
| 4 | Submits check-in | Streak increments; animated confirmation | Dopamine moment — celebrate the streak visually |
| 5 | Views updated dashboard | New entry appears; charts update in real time | Instant gratification — no refresh required |

### 5.4 Flow 3: Free-to-Pro Conversion

| Step | User Action | System Response | Conversion Strategy |
|------|-------------|----------------|---------------------|
| 1 | Completes 4th check-in | Upgrade prompt on confirmation screen | Show 1 AI insight preview — blurred — to build desire |
| 2 | Attempts 5th check-in | Hard paywall screen with plan comparison | Annual plan presented as 'Recommended' |
| 3 | Clicks 'Start Pro' | Stripe Checkout opens | Stripe-hosted; no custom payment form |
| 4 | Completes payment | Account upgraded; redirected to Pro dashboard | Welcome to Pro screen with what's now unlocked |
| 5 | First AI insight available | In-app notification + digest email | Reinforces purchase decision; first insight within first month |

### 5.5 Flow 4: MCP Client Onboarding \[MCP\]

| Step | User Action | System Response | Technical Note |
|------|-------------|----------------|----------------|
| 1 | Navigates to Settings > Integrations | Page shows 'AI Client Integration' card with MCP server URL and API key generation | Only visible to Pro users |
| 2 | Copies MCP server URL | URL copied: `https://mcp.reflect.app/v1/messages` | MCP SSE transport endpoint |
| 3 | Generates API key | Key displayed once with copy button | Stored as SHA-256 hash; never displayed again |
| 4 | Adds URL + key to AI client config | AI client now authenticated as this user | Client-side only; Reflect backend validates on first call |
| 5 | Uses Reflect tool inside AI client | Tool call authenticated; response returned | First successful tool call triggers in-app notification: 'Your AI client is connected' |
| 6 | Revokes access (optional) | Settings > Integrations 'Revoke' immediately invalidates key | Account deletion also revokes all active keys |

---

## 6. Data Requirements

### 6.1 Core Entities

| Entity | Key Fields | Notes |
|--------|-----------|-------|
| `users` | id, email, password_hash, plan (FREE\|PRO), stripe_customer_id, created_at, timezone | Password stored as bcrypt hash (cost 12). Stripe customer ID stored post-first payment intent. |
| `check_ins` | id, user_id, week_start_date, **wins**, **friction**, energy_rating (1–10), **signal_moment**, intentions, is_draft, submitted_at | **v1.2:** 'blockers' renamed to 'friction'; 'energy_notes' replaced by 'signal_moment'. Unique constraint on (user_id, week_start_date). |
| `insight_reports` | id, user_id, period_type (MONTHLY\|QUARTERLY), period_start, content (JSONB), generated_at, user_rating | Content JSONB now includes `interpersonal_themes` section sourced from signal_moment entries. Cached in Redis (30-day TTL). |
| `subscriptions` | id, user_id, stripe_subscription_id, plan (MONTHLY\|ANNUAL), status, current_period_end, cancelled_at | Single subscription per user. Stripe webhooks keep this in sync. |
| `api_keys` | id, user_id, key_hash (SHA-256), label, last_used_at, revoked_at, created_at | **\[MCP\]** Key shown once; stored as SHA-256. Revocable immediately. |
| `pattern_signals` | id, user_id, signal_type (RECURRING_FRICTION\|ENERGY_DROP\|AVOIDANCE\|INTERPERSONAL), signal_text, strength (1–10), week_start_date, nudge_sent_at, created_at | **\[Agent\]** Written by PatternSentinelAgent after each check-in. `nudge_sent_at` enforces 7-day cooldown. |
| `prompt_adaptations` | id, user_id, week_start_date, adaptation_text, included_in_email, created_at | **\[Agent\]** Written by PromptAdaptationAgent on Thursday. |
| `reminder_log` | id, user_id, sent_at, email_type | Deduplicates all email sends. Prevents duplicate Sunday reminders on Railway restart. |
| `accountability_links` | id, owner_user_id, partner_email, opted_in, created_at | Phase 3 feature. |

### 6.2 Data Retention & GDPR

- All check-in content classified as personal data under GDPR Article 4
- Data residency: EU region from launch (Neon EU-West, Upstash EU-West)
- Right to erasure: full deletion available in Settings; PII and check-in content hard-deleted within 30 days; all API keys revoked immediately on deletion request
- Data export: all check-in entries exportable as JSON or CSV on demand
- **\[MCP\]** AI processing disclosure: Claude API usage for insight generation disclosed in Privacy Policy; user data is sent to Anthropic API under commercial terms (no training consent granted)
- **\[MCP\]** API key data: key hash stored only; plaintext never persisted after display

---

## 7. Dependencies & Integrations

| Dependency | Type | Purpose | Risk if Unavailable |
|-----------|------|---------|---------------------|
| Anthropic Claude API | External API | Monthly/quarterly AI insight generation | High — core Pro value. Mitigated by Redis caching and graceful degradation |
| Stripe | External API | Subscription billing, checkout, customer portal | High — no revenue without it. Stripe SLA 99.99% |
| SendGrid | External API | Transactional email | Medium — reminders missed. Fallback: AWS SES |
| **\[MCP\]** MCP Protocol v1.0 | Protocol standard | Tool schema, SSE transport, message format | Medium — spec changes may require adapter updates |
| Neon (PostgreSQL) | Infrastructure | Primary data store | High — data unavailable. Neon automated backups |
| Upstash (Redis) | Infrastructure | AI insight caching, API key lookup cache | Low — insights regenerated on cache miss; DB fallback |
| Railway | Infrastructure | Spring Boot API hosting | High — app goes down. Railway auto-restart on crash |
| Vercel | Infrastructure | Next.js frontend hosting + CDN | Medium — frontend unavailable. 99.99% SLA |
| GitHub Actions | CI/CD | Automated test + deploy pipeline | Low — manual deploy possible |

---

## 8. Constraints & Assumptions

### 8.1 Constraints

- Development resource: sole founder with 4–8 hours per week
- Infrastructure budget: maximum £200/mo until MRR exceeds £1,000
- AI API cost: Anthropic Claude API costs must remain below 5% of MRR — enforced via Redis caching and model tiering
- Launch timeline: MVP (Phase 1) must ship within 6 weeks of development commencement
- No mobile native app in v1.0: PWA/responsive web only
- GDPR compliance is a hard requirement from Day 1
- **\[MCP\]** MCP server must share the same Spring Boot codebase as the REST API — no separate service (ADR-001)
- **\[MCP\]** MCP integration is Pro-only at launch; no technical mechanism should allow free-tier bypass at the API level

### 8.2 Assumptions

- Target users are English-speaking, comfortable with web-based SaaS tools, and willing to pay for personal productivity software
- Free-to-Pro conversion rate of 10% is achievable based on comparable productivity SaaS benchmarks (8–15% industry range)
- Claude API output quality for pattern generation is sufficient without fine-tuning — prompt engineering alone will produce acceptable quality for MVP
- Stripe Tax handles VAT calculation for EU/UK digital services without additional configuration
- **\[MCP\]** At least 20% of Pro subscribers are AI-native enough to connect Reflect to an AI client within the first 60 days of MCP availability — if this threshold is not reached, MCP marketing emphasis should be reduced
- **\[Agent\]** The `signal_moment` prompt produces sufficient interpersonal signal for PatternSentinelAgent after 4+ entries — to be validated in Phase 2 beta (Q9)

---

## 9. Risks & Open Questions

### 9.1 Product Risks

| Risk | Likelihood | Impact | Owner | Mitigation |
|------|-----------|--------|-------|-----------|
| AI insight quality too generic to convert free users to Pro | Medium | High | Engineering | Run 10 manual insight reviews before launch; A/B test prompt variants at Month 3 |
| Check-in prompts feel clinical — users drop off after entry 1 | Medium | High | Product | User test first 5 prompts with 5 target users before launch; iterate on tone |
| Free tier too generous — users never upgrade | Low | High | Product | 4-entry limit is firm; MCP is Pro-only and is a strong additional upgrade driver for the AI-native segment |
| Email open rates for Sunday reminders too low | Medium | Medium | Marketing | A/B test subject lines at Month 2; consider time-of-day personalisation |
| **\[MCP\]** OAuth implementation complexity delays Phase 2 | Medium | Medium | Engineering | Use Spring Security OAuth2 server; spike in Week 7 to validate before committing |
| **\[MCP\]** MCP spec evolves breaking changes | Low | Medium | Engineering | Pin to MCP v1.0; monitor spec changelog; 60-day upgrade SLO in NFRs |
| **\[MCP\]** AI clients hallucinate tool parameters — malformed check-ins stored | Medium | Medium | Engineering | Strict server-side validation on all tool inputs; return 400 with descriptive error |
| **\[Agent\]** signal_moment prompt insufficient interpersonal signal for PatternSentinelAgent | Medium | Medium | Engineering / Product | Validate with real data in Phase 2 beta; fallback to AVOIDANCE detection only if insufficient |

### 9.2 Open Questions

| # | Question | Owner | Resolution Date |
|---|----------|-------|----------------|
| Q2 | What is the minimum viable AI prompt structure to generate a high-quality monthly insight given only 5 prompts of input? | Engineering | End of Week 2 |
| Q3 | Should free-tier users see trend charts (blurred past 4) or no charts at all? | Product | **Resolved: blurred charts** |
| Q4 | What Sunday reminder time optimises open rate and check-in completion? | Product | **Resolved: Sunday 09:00 local** |
| Q5 | Is Stripe Tax sufficient for EU VAT handling post-Brexit? | Engineering / Legal | Pre-Stripe integration |
| **\[MCP\] Q6** | Should the MCP server use SSE or WebSocket transport at launch? | Engineering | **Resolved: SSE (ADR-004)** |
| **\[MCP\] Q7** | Should Reflect be listed in a public MCP directory at launch? | Product / Marketing | Pre-Phase 2 ship |
| **\[MCP\] Q8** | API key vs OAuth for initial MCP authentication? | Engineering / Product | **Resolved: API key (ADR-003)** |
| **\[Agent\] Q9** | Does the signal_moment prompt produce sufficient interpersonal signal for PatternSentinelAgent? What minimum entry count is required before agents activate? | Engineering / Product | End of Phase 2 beta |
| **\[Agent\] Q10** | Should PromptAdaptationAgent modify the prompt label itself, or only append a contextual sentence to the Sunday reminder email? | Product / Engineering | Before US-16 implementation |

---

## 10. Timeline & Milestones

| Milestone | Target | Exit Criteria |
|-----------|--------|---------------|
| PRD v1.2 approved & Design brief issued | Week 1 | All P0 open questions resolved; MCP and agent decisions documented |
| Phase 1 engineering kickoff | Week 1 | GitHub repo created; Railway + Neon + Upstash + Vercel configured; CI pipeline green |
| Check-in form + auth complete (internal) | Week 3 | US-01, US-02, US-03 acceptance criteria all pass |
| Dashboard + billing complete (internal) | Week 5 | US-05, US-09 pass; Stripe test mode confirmed |
| MVP soft launch (beta users) | Week 6 | 10 invited beta users onboarded; < 2 P0 bugs open |
| Product Hunt launch | Week 8 | Landing page live; SEO meta complete |
| AI Insight Engine live (Phase 2 start) | Week 7 | US-06 acceptance criteria pass; first real insight generated and reviewed |
| **\[MCP\]** MCP SSE spike complete | Week 2 | Spring Boot SSE endpoint validates against Claude.ai in a real session |
| **\[MCP\]** MCP adapter layer built & tested | Week 9 | All 4 MCP tools implemented; ≥ 90% unit test coverage; contract tests pass |
| **\[MCP\]** MCP shipped to Pro users | Week 11 | US-12 through US-15 all pass in production |
| AI Insights shipped to Pro users | Week 11 | First monthly insight batch generated for all eligible Pro users |
| Phase 2 complete | Week 12 | All Phase 2 stories live and monitored |
| Phase 3 complete (growth + agent features) | Week 20 | US-10, US-11, US-16, US-17 shipped; annual plan live; referral system active |

---

## Appendix

### A. Feature Backlog — Icebox (P3)

| Feature | Rationale for Deferral |
|---------|----------------------|
| Native iOS / Android app | PWA covers mobile use case at launch |
| Custom prompt templates | Risk of blank-page problem returning; structure is the product's differentiator |
| Jira / Linear / Notion integration | Phase 4 Teams feature |
| Voice memo check-ins | High build complexity; low signal quality for AI parsing |
| **\[MCP\]** Webhook / event streaming via MCP | SSE request/response sufficient for v1.1 use cases |
| **\[MCP\]** Team MCP tools | Requires Teams plan (Phase 4) |
| **\[Agent\]** Agent activation on free tier | Data moat strategy — agents are the primary Pro retention mechanism |
| Coaching marketplace | Year 3+ feature |

### B. Glossary

| Term | Definition |
|------|-----------|
| Check-in | A single completed weekly review entry comprising all 5 structured prompts |
| Streak | The consecutive count of weeks in which a check-in was submitted |
| AI Insight Report | An AI-generated summary of patterns detected across a user's check-in history |
| Signal moment | The new prompt 4 field: "What interaction or moment from this week is still on your mind — and why?" |
| Friction | The renamed prompt 2 field (was 'blockers'): captures avoidance and interpersonal resistance |
| Pattern signal | A row in `pattern_signals` written by PatternSentinelAgent after detecting a meaningful pattern |
| MCP | Model Context Protocol — an open standard for exposing structured tool actions to AI clients |
| MCP Server | The Reflect-hosted endpoint implementing the MCP protocol and exposing 4 tools |
| RICE | Prioritisation framework: Reach × Impact × Confidence ÷ Effort |
| WAU | Weekly Active Users — proportion of Pro subscribers completing a check-in in a given week |
| Churn | Monthly rate at which Pro subscribers cancel their subscription |
| P0/P1/P2/P3 | Priority tiers: P0 = launch blocker; P1 = should have; P2 = nice to have; P3 = icebox |
