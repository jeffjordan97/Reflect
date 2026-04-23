# User Profile & Goals — Design Spec

**Date:** 2026-04-23
**Status:** Proposed
**Scope:** User profile context, goal tracking, AI-enhanced insights, calm gamification

## Overview

Transform Reflect from "weekly journal with AI commentary" into a personal growth companion where check-ins connect to the user's aspirations. Users provide context about themselves (profession, goals, focus areas) and the AI uses this to generate specific, goal-aware reflections.

## Research Foundation

This design is informed by evidence-based psychology research:

| Framework | Researcher | Key Finding Applied |
|-----------|-----------|-------------------|
| Implementation Intentions | Gollwitzer (1999) | "If-then" plans bridge the intention-action gap. Nudge users toward specific intentions. |
| Goal-Setting Theory | Locke & Latham (2002) | Goals + feedback are multiplicative. Close the loop between intentions and results. |
| Self-Determination Theory | Deci & Ryan (2000) | Autonomy, competence, relatedness sustain motivation. Never prescribe; show progress. |
| Motivational Interviewing | Miller & Rollnick (2002) | People act on their own change talk. Reflect back, don't advise. |
| Solution-Focused Brief Therapy | de Shazer & Berg | Exception-finding: notice when the problem was absent. |
| Self-Compassion | Neff (2023) | Better motivator than self-criticism. Normalise difficulty. |
| Mental Contrasting / WOOP | Oettingen (2012) | Pure positive visualisation decreases action. Confront obstacles honestly. |
| Fresh Start Effect | Milkman et al. (2014) | Temporal landmarks (new week) enable clean-slate motivation. |
| Approach vs. Avoidance Goals | Neuroscience (2024) | Approach framing activates reward circuits; avoidance activates anxiety. |
| AI Empathy Research | Frontiers (2025) | Cognitive empathy (accurate understanding) trusted; affective empathy ("I feel for you") triggers uncanny valley. |

## Data Model

### user_profiles (free for all users)
One-to-one with users. Fields: profession, industry, role_level, focus_areas (array), bio_context (free text).

### goals (Pro only)
Fields: title, description, horizon (SHORT/MEDIUM/LONG), status (ACTIVE/PAUSED/COMPLETED/RELEASED), optional target_date. Max 7 active goals.

### goal_reflections
Links check-ins to goals with resonance classification: ADVANCING, STRUGGLING, NEUTRAL, PIVOTING. Populated by AI (lightweight Haiku classification call) and overridable by user.

### goal_milestones
User-created or AI-suggested moments worth celebrating within a goal.

## Progress Model: Resonance, Not Percentage

For subjective goals ("become a better leader"), percentage-complete is meaningless. Resonance captures the emotional relationship between a check-in and a goal. Progress is the accumulating story of resonances over time, not a number going up.

## Gamification: Calm, Not Corporate

- **Resonance rings** — dots coloured by resonance, growing over time
- **Momentum labels** — one word: "Building", "Working through it", "Steady", "Shifting"
- **Milestone moments** — evidence of growth for when you doubt progress
- **Goal completion celebration** — confetti + AI reflection on the journey
- **No penalties for gaps** — no guilt language, no streak-loss framing
- **No badges, points, or achievements** — the insight is the reward

## AI Prompt Enhancements (already implemented)

System prompt updated with:
- Self-compassion guidance for hard weeks
- Change talk amplification (Motivational Interviewing)
- Exception-finding (SFBT)
- Prohibition on simulated empathy
- Feedback loop: previous week's intentions included in context

## AI Prompt Enhancements (planned for Phase A)

Dynamic system prompt construction that includes:
- User profile context (profession, focus areas, bio)
- Active goals with titles and descriptions
- Instruction: "If this week's check-in connects to any goal, briefly note the connection. Do not force a connection if none exists."

## Implementation Phases

### Phase A — MVP (2-3 weeks)
Profile CRUD + Goals CRUD + dynamic system prompt. No AI classification yet.

### Phase B — AI Resonance (1-2 weeks)
GoalReflectionService + structured classification + "This week and your goals" UI.

### Phase C — Gamification Visuals (1-2 weeks)
Resonance rings, milestones, goal completion celebration, goals summary.

### Phase D — Agent Integration (Phase 3)
Goals feed into PatternSentinelAgent, InsightSynthesisAgent, NudgeAgent.

## Tier Strategy

| Feature | Free | Pro |
|---------|------|-----|
| Profile ("About you") | Yes | Yes |
| Enhanced AI insights (profile context) | Yes | Yes |
| Goals | No | Yes |
| Goal resonance tracking | No | Yes |

## Open Questions

1. Profile prompt timing: after 2nd check-in (recommended) or during onboarding?
2. Max active goals: 7 (recommended) or uncapped?
3. Build Phase A now, or continue with monthly insights first?
