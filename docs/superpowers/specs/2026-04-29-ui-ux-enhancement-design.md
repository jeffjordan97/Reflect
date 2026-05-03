# UI/UX Enhancement Design — 21st.dev-Inspired, Calm & Minimal

**Date:** 2026-04-29
**Status:** Draft
**Approach:** Inspiration-only from 21st.dev community components, re-implemented in existing Ark UI + Tailwind 3.4 stack. No new UI framework dependencies.

---

## Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Visual mood | Calm & Minimal | Reflect is a journaling tool — the UI should feel grounding, not stimulating |
| Primary green | `#34D399` (Emerald-400) | Visible but not loud. Reads calm on light backgrounds |
| Dependency changes | None | Re-implement patterns using Ark UI + Tailwind 3.4. No Framer Motion, no shadcn/ui migration |
| Mobile priority | Mobile-first, desktop-enhanced | Every component designed mobile-first, desktop layout is the enhancement |
| Streak icon | Subtle dot or leaf — no fire emoji | Fire emoji conflicts with the calm aesthetic |

---

## Phase 0 — Design System Alignment (Foundation)

All subsequent phases depend on this. Changes cascade across every page via Tailwind config and CSS variables.

### Tailwind Config Overhaul

Replace the current indigo primary palette with an Emerald scale centered on `#34D399`:

```
primary-50:  #ECFDF5
primary-100: #D1FAE5
primary-200: #A7F3D0
primary-300: #6EE7B7
primary-400: #34D399  ← default interactive (buttons, links, focus rings)
primary-500: #10B981  ← hover state
primary-600: #059669  ← active/pressed
primary-700: #047857
primary-800: #065F46
primary-900: #064E3B
```

Semantic color tokens:

```
text-primary:    #0F172A  (Slate-900)
text-secondary:  #475569  (Slate-600)
text-muted:      #94A3B8  (Slate-400)
bg-canvas:       #FAFAF9  (warm off-white)
bg-surface:      #FFFFFF
border-default:  #E2E8F0  (Slate-200)
border-input:    #94A3B8  (Slate-400, WCAG fix)
```

Accent tokens:

```
amber-500:  #F59E0B  (streaks, milestones)
red-600:    #DC2626  (destructive actions)
purple-600: #7C3AED  (AI/MCP features)
```

### Font Loading

Load via `next/font/google` in `layout.tsx`:

- **DM Sans** (400, 500, 600) — UI font. Navigation, labels, body text
- **Fraunces** (400, 600) — Display font. Page titles, check-in prompts, streak numbers
- **DM Mono** (400) — Code font. Timestamps, tokens, step counters

CSS variables: `--font-sans` (DM Sans), `--font-serif` (Fraunces), `--font-mono` (DM Mono)

Tailwind mapping:
```
font-sans:  ["var(--font-sans)", "system-ui", "sans-serif"]
font-serif: ["var(--font-serif)", "Georgia", "serif"]
font-mono:  ["var(--font-mono)", "Courier", "monospace"]
```

### Canvas & Spacing

- Body background: `bg-canvas` (#FAFAF9)
- 4px grid spacing scale from design system doc (4px to 64px, 12 steps)
- Consistent border radius: 12px for cards, 10px for inputs/buttons, 8px for badges

### Animation Keyframes

Add to Tailwind config (used across all phases):

```
keyframes:
  slide-in-right:  0% { translateX(30px), opacity 0 } → 100% { translateX(0), opacity 1 }
  slide-out-left:  0% { translateX(0), opacity 1 } → 100% { translateX(-30px), opacity 0 }
  fade-in:         0% { opacity 0 } → 100% { opacity 1 }
  shimmer:         0% { backgroundPosition -200% 0 } → 100% { backgroundPosition 200% 0 }
  draw-circle:     stroke-dashoffset 166 → 0
  draw-check:      stroke-dashoffset 48 → 0

animations:
  step-enter:   slide-in-right 300ms ease-out
  step-exit:    slide-out-left 200ms ease-in
  fade-in:      fade-in 300ms ease-out
  shimmer:      shimmer 1.5s ease-in-out infinite
```

### WCAG Compliance Fix

- Input borders: change from `slate-200` (#E2E8F0) to `slate-400` (#94A3B8) — fixes contrast ratio from 1.4:1 to 3:1+ against the canvas/surface backgrounds

---

## Phase 1 — Check-in Wizard

**Inspiration:** Shadcn Studio multi-step forms, MagicUI card patterns, Aceternity step loaders.

### Mobile Layout (Primary)

- Full-width card with 16px horizontal padding
- Step dots above card, horizontally centered
- Textarea fills available width
- Back/Continue buttons span full width, stacked vertically on very small screens (< 360px)
- Step counter: "1 of 5" text + thin progress bar fallback below dots

### Desktop Layout

- Centered card (max-width 520px) floating on canvas with generous vertical whitespace
- `min-h-[60vh] flex items-center justify-center` wrapper
- Card: `bg-surface`, `border-default`, `rounded-2xl`, subtle shadow (`shadow-sm`)

### Connected-Dot Progress Indicator

Replaces the current thin progress bar:

- 5 dots connected by short lines
- Completed dots: filled `primary-400` with checkmark icon
- Active dot: `primary-400/20` background + `ring-2 ring-primary-400` pulse
- Future dots: `bg-muted` (Slate-200)
- Connecting lines: filled `primary-400` when completed, `bg-muted` otherwise
- `transition-colors duration-300` on dots, `duration-500` on lines
- Mobile: dots are 8px, lines are 16px. Desktop: dots are 10px, lines are 24px

### Card-Per-Step Layout

Each of the 5 check-in prompts renders inside a single centered card:

```
[Step counter — mono, uppercase, muted]
[Category label — primary-400, small]
[Prompt question — Fraunces serif, 20-22px, text-primary]
[Subtitle — DM Sans, 13px, text-secondary]
[Input area — textarea or energy slider]
[Back / Continue buttons]
```

### Slide Transitions

Use `@headlessui/react` `<Transition>` component (Tailwind-native, ~3KB). This is compatible with Ark UI — Ark UI provides accessible primitives (Slider, Dialog), while Headless UI Transition is used solely for enter/leave animations. No overlap.

- Forward: current step slides out left, next step slides in from right (300ms ease-out)
- Backward: current step slides out right, previous step slides in from left (300ms ease-out)
- CSS classes via Tailwind: `translate-x-8 opacity-0` → `translate-x-0 opacity-100`
- Fallback: if Headless UI Transition causes issues with Ark UI, use CSS-only approach with `tailwindcss-animate` plugin + conditional class toggling via React state

### Fraunces for Prompts

- Prompt question ("What moved forward this week?"): `font-serif text-xl font-semibold`
- Step counter: `font-mono text-xs uppercase tracking-wider text-muted`
- Category label: `text-xs font-medium text-primary-400`
- Subtitle: `font-sans text-sm text-secondary`

### Refined Textarea

- Background: `bg-canvas` (#FAFAF9) inside the white card — subtle depth
- Border: `border-input` (slate-400) for WCAG contrast
- Border radius: `rounded-xl` (12px)
- Padding: `px-4 py-3`
- Focus state: `focus:border-primary-400 focus:ring-1 focus:ring-primary-400`
- Placeholder: `text-muted` ("Take your time...")

### Completion Moment

After step 5 submit, instead of immediate redirect:

1. Card content fades out (200ms)
2. Animated SVG checkmark draws itself center-card:
   - Circle draws via `stroke-dashoffset` animation (0.6s ease-out)
   - Checkmark path draws inside (0.3s ease-out, 0.6s delay)
   - Stroke color: `primary-400`
3. Background briefly warms to `primary-50` (#ECFDF5) — 300ms fade
4. Summary text fades in below checkmark: "Check-in complete. Week {streak}."
   - Streak number uses NumberTicker animation (requestAnimationFrame, ease-out cubic, ~500ms)
5. After 2 seconds, auto-redirect to `/history/{id}`

No confetti. No fireworks. A calm acknowledgement.

---

## Phase 2 — History Timeline

**Inspiration:** shadcn-timeline, Shadcnblocks timeline blocks, ReUI timeline.

### Mobile Layout (Primary)

- Full-width cards with 16px padding
- Vertical timeline line: 1px, left-aligned at 8px from left edge
- Timeline dots: 8px, positioned on the line
- Cards: full width minus timeline gutter (padding-left 28px)
- Tap to expand — card grows to show full check-in detail inline

### Desktop Layout

- Max-width 640px, centered
- Timeline dots: 10px
- Cards have slightly more padding (20px)
- Hover state on cards: `shadow-sm` → `shadow-md` transition (200ms)

### Vertical Timeline

- Continuous vertical line: `absolute left-[8px] top-0 bottom-0 w-px bg-border-default`
- Dots positioned on the line with `ring-4 ring-bg-canvas` to punch through
- Dot color based on energy rating:
  - 1–3: `red-400` (#F87171)
  - 4–6: `amber-400` (#FBBF24)
  - 7–10: `primary-400` (#34D399)

### Timeline Card

```
[Date — mono, text-muted]              [Energy badge — colored pill]
[Wins preview — text-primary, 14px, line-clamp-2]
```

- Background: `bg-surface`, border: `border-default`, radius: `rounded-xl`
- Subtle shadow: `shadow-[0_1px_2px_rgba(0,0,0,0.03)]`

### Expandable Detail

On tap/click, the card expands inline to show all 5 fields:

- CSS `max-height` transition (300ms ease-out) + `overflow-hidden`
- Expanded view shows all fields with labels in `text-xs uppercase text-muted`
- Collapse button at bottom: "Show less"
- No page navigation needed for reading — `/history/[id]` remains for direct links

### Staggered Entry Animation

Cards fade in with incremental delay:

- Each card: `animate-fade-in` with `animation-delay: ${index * 100}ms`
- `animation-fill-mode: backwards` so cards start invisible
- Only the first 10 cards animate; cards beyond that appear immediately (performance)

### Page Heading

- "Your Journey" in `font-serif text-2xl font-semibold text-primary` (Fraunces)
- Mobile: `text-xl`

### Empty State

When no check-ins exist:

```
[Clipboard icon — 32px, text-muted, inside rounded-full bg-muted p-4]
"No check-ins yet"
"Start your first weekly review to build self-awareness."
[Start your first check-in — primary button]
```

- Container: `border-dashed border-border-default rounded-xl p-8 text-center`
- `min-h-[300px] flex flex-col items-center justify-center`

---

## Phase 3 — Insight Presentation

**Inspiration:** Shadcn Studio statistics cards, MagicUI number ticker, inline SVG sparklines.

### Mobile Layout (Primary)

- Full-width cards, stacked vertically, 12px gap
- Monthly synthesis card is always first (most important)
- Energy trend chart: full-width bar chart, 4 bars
- Pattern signal cards: compact, tags wrap on small screens

### Desktop Layout

- Max-width 640px, centered (consistent with history page)
- Two-column grid for pattern signal cards (when 2+ exist)
- Monthly synthesis card: full width, spans both columns

### Monthly Synthesis Card (AI)

Distinguished by purple accent:

```
[AI icon (✦ or Sparkles) — purple-100 bg, purple-600 icon]  [MONTHLY SYNTHESIS — purple-600 uppercase]  [April 2026 — mono, muted]
[Synthesis text — text-primary, 14px, line-height 1.6]
```

- Border: `border-purple-200`
- Shadow: `shadow-[0_1px_2px_rgba(124,58,237,0.06)]` (purple-tinted)
- Background: `bg-surface`

### Pattern Signal Cards

```
[Trend icon (↗/↘/→) — primary-50 bg]  [Pattern title — font-medium, text-primary]
[Description — text-secondary, 13px]
[Tags: field origin, frequency — bg-slate-100 text-slate-600, rounded-lg]
```

- Standard card styling (no purple accent)
- Tags wrap naturally on mobile

### Energy Trend Card

```
[Energy Trend — font-medium]                    [Average — large number, NumberTicker]
[4-bar chart — color-coded by energy level]
[W1   W2   W3   W4 — mono labels]
```

- Bar chart: simple `div` bars with `flex items-end gap-1.5`, height percentage based on rating/10
- Bar colors match timeline dot scheme (red/amber/green)
- Average number: `text-2xl font-bold tabular-nums` with NumberTicker animation on mount
- Trend indicator: `+0.8 vs last month` in `text-primary-500 text-sm font-medium`

### Empty State

When fewer than 4 check-ins:

```
[Sparkles icon — muted]
"Insights unlock after 4 check-ins"
[Progress ring — SVG circle, stroke-dasharray showing X/4 progress]
"{X} of 4 complete"
```

- Progress ring: 48px diameter, `primary-400` fill, `bg-muted` track

---

## Phase 4 — Account & Settings

**Inspiration:** Shadcn Studio account settings, sectioned card layouts.

### Mobile Layout (Primary)

- Full-width stacked cards, 12px gap
- Streak hero: centered text, full width
- Each settings group: its own card

### Desktop Layout

- Max-width 520px, centered
- Same stacked layout — no sidebar tabs (too few settings to justify)

### Streak Hero Section

```
[Large streak number — Fraunces, text-4xl font-bold, NumberTicker]
"week streak"
[Status badge — emerald bg if active, muted if broken]  [Next milestone: Xw — muted badge]
```

- Streak icon: a small filled circle (`●`) in `primary-400` — no fire emoji
- Status badge: "On track" (green), "Streak broken" (muted/red), "Just started" (amber)
- Milestone badges at 4, 12, 26, 52 weeks
- Card: `bg-surface rounded-2xl p-6 text-center`

### Plan Status Card

```
[Plan name — font-medium]              [Upgrade — primary button, compact]
[Usage — "3 of 4 check-ins this month"]
[Thin progress bar — primary-400 fill]
```

- Progress bar: `h-1 rounded-full bg-muted` track, `bg-primary-400` fill
- When at limit: bar turns `amber-500`, text changes to "Limit reached"

### Settings Sections

Each in its own card:

- **Profile** — email (read-only display), display name
- **Subscription** — plan status card (above)
- **Preferences** — reminder day/time (future), notification opt-in (future)
- **Danger zone** — delete account button, `text-red-600` with confirmation dialog

---

## Phase 5 — Auth Pages

**Inspiration:** shadcn/ui authentication example, Shadcn.io floating labels, split layouts.

### Mobile Layout (Primary)

- Form only — no brand panel
- Full-width form, 16px horizontal padding, centered vertically
- Logo + tagline above form
- Inputs: full width, floating labels
- Primary CTA: full width button

### Desktop Layout

- Split: `grid lg:grid-cols-2 min-h-screen`
- Left: brand panel — dark gradient (`from-primary-900 to-slate-900`), Reflect logo in Fraunces, testimonial quote at bottom
- Right: form panel — `bg-canvas`, centered form (max-width 360px)
- Brand panel hidden on mobile: `hidden lg:flex`

### Shared Layout

All auth pages (login, register, forgot-password, reset-password, verify-email) share the split layout. The form content varies; the structure doesn't.

### Floating Labels

CSS-only via Tailwind `peer` selectors:

- Input has `placeholder=" "` (space)
- Label uses `peer-placeholder-shown:translate-y-0` (inside input) → `peer-focus:-translate-y-3 peer-focus:scale-[0.85]` (lifted)
- `peer-[:not(:placeholder-shown)]:-translate-y-3` keeps label lifted when field has content
- `transition-all duration-200`
- Focus state: label turns `text-primary-400`

### Copy Tone

- Login: "Welcome back" / "Sign in to continue your practice"
- Register: "Start reflecting" / "Create your account to begin"
- Forgot password: "Reset your password" / "We'll send you a link"
- Consistent with the calm, warm brand voice

---

## Phase 6 — Global Chrome

**Inspiration:** Frosted glass headers, shimmer skeletons, empty state patterns.

### Frosted Glass Header

- Default: `bg-transparent`
- On scroll (>10px): `bg-canvas/80 backdrop-blur-md shadow-sm border-b border-default`
- Transition: `transition-all duration-300`
- Scroll detection: `useEffect` with passive scroll listener
- Mobile: compact, logo left, hamburger right (or bottom tab nav)
- Desktop: logo left, nav links center, streak badge right

### Streak Badge in Navigation

- Compact pill: `bg-amber-50 text-amber-800 px-2.5 py-0.5 rounded-lg text-xs font-semibold`
- Streak number + small filled dot (`●`) in `primary-400` as icon — no fire emoji
- Format: `● 12` 
- Zero state: hidden or `text-muted` with "Start" label

### Mobile Navigation

Bottom tab bar (fixed):

```
[Check-in]  [History]  [Insights]  [Account]
```

- `fixed bottom-0 left-0 right-0 bg-surface/90 backdrop-blur-sm border-t border-default`
- Active tab: `text-primary-400` + 2px top border
- Inactive: `text-muted`
- Icons from Lucide React: `PenLine`, `Clock`, `Sparkles`, `User`
- Safe area padding for iOS: `pb-[env(safe-area-inset-bottom)]`
- Header hides nav links on mobile — replaced by bottom tabs

### Loading Skeletons

Shimmer effect for all data-loading states:

```css
background: linear-gradient(90deg, hsl(var(--muted)) 25%, hsl(var(--muted)/0.5) 50%, hsl(var(--muted)) 75%);
background-size: 200% 100%;
animation: shimmer 1.5s ease-in-out infinite;
```

Skeleton shapes match the actual content:
- History page: timeline with 3 skeleton cards (rectangle for date, wider rectangle for text, small circle for energy badge)
- Insights page: 2 skeleton cards (header rectangle, body lines)
- Account page: streak number circle, plan card rectangle

### Empty States

Consistent pattern across all pages:

```
[Icon — Lucide, 32px, text-muted, inside rounded-full bg-muted/50 p-4]
[Heading — font-medium, text-primary]
[Description — text-sm text-secondary, max-w-sm, centered]
[CTA button — primary, when actionable]
```

Container: `min-h-[300px] flex flex-col items-center justify-center gap-3 border border-dashed border-border-default rounded-xl p-8`

Specific states:
- No check-ins: ClipboardList icon, "No check-ins yet", "Start your first check-in" CTA
- No insights: Sparkles icon, "Insights unlock after 4 check-ins", progress ring (X/4)
- Streak zero: Calendar icon, "Your streak starts with your next check-in"
- Streak broken: Calendar icon, "Your streak will restart with your next check-in"

### Page Transitions

Subtle opacity fade on route changes:

- Wrap page content in a `key={pathname}` container with `animate-fade-in` (300ms)
- No sliding — just a gentle opacity transition to avoid visual noise

### Consistent Design Tokens

Applied globally across all phases:

| Element | Border Radius | 
|---------|--------------|
| Cards | 12px (`rounded-xl`) |
| Inputs, buttons | 10px (`rounded-[10px]`) |
| Badges, pills | 8px (`rounded-lg`) |
| Dots, avatars | 50% (`rounded-full`) |

---

## Bundle Impact

| Addition | Size | Purpose |
|----------|------|---------|
| `@headlessui/react` Transition | ~3KB gzip | Step transitions (may already be installed) |
| `canvas-confetti` | Not used | Removed — calm completion instead |
| DM Sans font | ~15KB | UI typography |
| Fraunces font | ~12KB | Display typography |
| DM Mono font | ~8KB | Monospace typography |
| Custom CSS keyframes | 0KB | Compile-time via Tailwind config |
| NumberTicker utility | 0KB | ~20 lines of vanilla React |
| SVG checkmark | 0KB | Inline SVG + CSS animation |

**Total added JS:** ~3KB (Headless UI Transition only)
**Total added fonts:** ~35KB (loaded async, display=swap)

---

## What This Plan Does NOT Include

- No new UI framework (no shadcn/ui, no Radix migration)
- No Framer Motion
- No React 19 or Tailwind v4 upgrade
- No dark mode (separate effort)
- No new pages or routes
- No API changes
- No backend work
