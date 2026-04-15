# Reflect Design System v1.0

| Field | Value |
|-------|-------|
| Version | 1.0 |
| Status | Draft |
| Product | Reflect — Guided Weekly Review & AI Insight Platform |
| Author | Jeffrey Jordan |
| Date | April 2026 |
| Companion Docs | PRD v1.2, SDD v1.2 |

## Purpose & Audience

This document is the single source of truth for Reflect's visual and interaction language. It defines the three-tier token architecture (primitive, semantic, component), the full component library, accessibility requirements, and usage guidelines. It is intended for the product engineer (building the Next.js frontend) and any future designers or contributors.

### Changelog

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Apr 2026 | Initial release — full token system, component library, accessibility baseline |

---

## 1. Design Principles

These five principles govern every design decision in Reflect. When trade-offs arise, return to this list.

| Principle | What it means in practice |
|-----------|--------------------------|
| 1. Ritual over feature | The Sunday check-in is a ritual, not a form. Every UI decision should make it feel intentional and calm — not clinical or productivity-tool-generic. Prioritise whitespace, unhurried pacing, and focused prompts. |
| 2. Earn the upgrade | Free users should feel the product's quality even before they pay. The paywall should feel like an invitation, not a wall. Design free states with the same care as Pro states. |
| 3. Data serves the person | Trend charts and AI insights exist to help users understand themselves — not to impress. Visualisations must be legible at a glance. Avoid chart clutter; one insight per visual element. |
| 4. Accessible by default | WCAG 2.1 AA is the baseline, not an enhancement. Every colour pair must pass 4.5:1 contrast. Every interactive element must be keyboard-navigable. Every icon must have a text label. |
| 5. Mobile-first, desktop-enhanced | The check-in form must be excellent on a Sunday morning on a phone. Desktop layouts use the additional space for charts and history — they do not redesign the mobile experience. |

---

## 2. Token Architecture

Reflect uses a three-tier token architecture. Tokens must always be consumed from the highest appropriate tier — never use a primitive token directly in a component if a semantic token exists.

| Tier | Purpose | Example | Rule |
|------|---------|---------|------|
| Tier 1 — Primitive | Raw values. No semantic meaning. Never reference these in component styles. | `--color-slate-900` | Used only as the value of semantic tokens |
| Tier 2 — Semantic | Named by purpose, not appearance. These are what component styles reference. | `--color-text-primary` | Used in component CSS; never hardcode hex values |
| Tier 3 — Component | Component-specific aliases. Used when a component needs a value that is specific to its context. | `--checkin-form-bg` | Used only within that component's stylesheet |

### Tier 1: Primitive Tokens

```css
:root {
  /* Slate */
  --primitive-slate-50:  #F8FAFC;
  --primitive-slate-100: #F1F5F9;
  --primitive-slate-200: #E2E8F0;
  --primitive-slate-600: #475569;
  --primitive-slate-900: #0F172A;

  /* Green (brand) */
  --primitive-green-50:  #F0FDF4;
  --primitive-green-100: #DCFCE7;
  --primitive-green-500: #22C55E;
  --primitive-green-600: #16A34A;
  --primitive-green-700: #15803D;
  --primitive-green-900: #14532D;

  /* Amber (accent) */
  --primitive-amber-400: #FBBF24;
  --primitive-amber-500: #F59E0B;
  --primitive-amber-600: #D97706;

  /* Red (destructive) */
  --primitive-red-50:    #FFF1F2;
  --primitive-red-600:   #DC2626;
  --primitive-red-700:   #B91C1C;

  /* Purple (MCP / AI indicator) */
  --primitive-purple-100: #EDE9FE;
  --primitive-purple-600: #7C3AED;

  /* Spacing scale (multiples of 4px) */
  --primitive-space-1:  4px;
  --primitive-space-2:  8px;
  --primitive-space-3:  12px;
  --primitive-space-4:  16px;
  --primitive-space-5:  20px;
  --primitive-space-6:  24px;
  --primitive-space-8:  32px;
  --primitive-space-10: 40px;
  --primitive-space-12: 48px;
  --primitive-space-16: 64px;

  /* Radius */
  --primitive-radius-sm:   4px;
  --primitive-radius-md:   8px;
  --primitive-radius-lg:   12px;
  --primitive-radius-xl:   16px;
  --primitive-radius-full: 9999px;
}
```

### Tier 2: Semantic Tokens

```css
:root {
  /* Text */
  --color-text-primary:    var(--primitive-slate-900);
  --color-text-secondary:  var(--primitive-slate-600);
  --color-text-muted:      #94A3B8;
  --color-text-inverse:    #FFFFFF;
  --color-text-brand:      var(--primitive-green-700);
  --color-text-destructive:var(--primitive-red-600);

  /* Backgrounds */
  --color-bg-canvas:       #FAFAF9;
  --color-bg-surface:      #FFFFFF;
  --color-bg-subtle:       var(--primitive-slate-50);
  --color-bg-muted:        var(--primitive-slate-100);
  --color-bg-brand:        var(--primitive-green-500);
  --color-bg-brand-subtle: var(--primitive-green-50);
  --color-bg-destructive:  var(--primitive-red-50);
  --color-bg-ai:           var(--primitive-purple-100);

  /* Border */
  --color-border-default:  var(--primitive-slate-200);
  --color-border-strong:   #CBD5E1;
  --color-border-brand:    var(--primitive-green-600);
  --color-border-focus:    var(--primitive-green-600);
  --color-border-error:    var(--primitive-red-600);

  /* Interactive */
  --color-interactive-primary:       var(--primitive-green-600);
  --color-interactive-primary-hover: var(--primitive-green-700);
  --color-interactive-primary-text:  #FFFFFF;
  --color-interactive-secondary:     var(--primitive-slate-100);
  --color-interactive-secondary-hover: var(--primitive-slate-200);

  /* Feedback */
  --color-feedback-success: var(--primitive-green-600);
  --color-feedback-warning: var(--primitive-amber-500);
  --color-feedback-error:   var(--primitive-red-600);
  --color-feedback-info:    #2563EB;

  /* Spacing semantic aliases */
  --spacing-xs:   var(--primitive-space-1);  /* 4px  */
  --spacing-sm:   var(--primitive-space-2);  /* 8px  */
  --spacing-md:   var(--primitive-space-4);  /* 16px */
  --spacing-lg:   var(--primitive-space-6);  /* 24px */
  --spacing-xl:   var(--primitive-space-8);  /* 32px */
  --spacing-2xl:  var(--primitive-space-12); /* 48px */
  --spacing-3xl:  var(--primitive-space-16); /* 64px */

  /* Radius semantic aliases */
  --radius-sm:   var(--primitive-radius-sm);
  --radius-md:   var(--primitive-radius-md);
  --radius-lg:   var(--primitive-radius-lg);
  --radius-full: var(--primitive-radius-full);
}
```

---

## 3. Colour System

### 3.1 Brand Palette

Reflect's brand colour is forest green — chosen for its associations with growth, calm, and nature. It avoids the overused blue-grey palette of most productivity tools. Amber is the accent, used sparingly for energy-state indicators and streak highlights.

**Green (Brand)**

| Swatch | Hex | Token | Usage |
|--------|-----|-------|-------|
| Green-900 | #14532D | `--primitive-green-900` | Dark text on light bg |
| Green-700 | #15803D | `--color-text-brand` | Brand text, active states |
| Green-600 | #16A34A | `--color-interactive-primary` | Primary buttons, focus rings |
| Green-500 | #22C55E | `--color-bg-brand` | Filled button bg |
| Green-100 | #DCFCE7 | `--primitive-green-100` | Subtle brand tints |
| Green-50 | #F0FDF4 | `--color-bg-brand-subtle` | Feature card backgrounds |

**Slate (Neutral)**

| Swatch | Hex | Token | Usage |
|--------|-----|-------|-------|
| Slate-900 | #0F172A | `--color-text-primary` | Body text, headings |
| Slate-600 | #475569 | `--color-text-secondary` | Secondary text, labels |
| Slate-400 | #94A3B8 | `--color-text-muted` | Placeholder, helper text |
| Slate-200 | #E2E8F0 | `--color-border-default` | Dividers, card borders |
| Slate-100 | #F1F5F9 | `--color-bg-muted` | Input backgrounds |
| Canvas | #FAFAF9 | `--color-bg-canvas` | Page background |

**Accent & Feedback**

| Swatch | Hex | Token | Usage |
|--------|-----|-------|-------|
| Amber-500 | #F59E0B | `--color-feedback-warning` | Streak milestone, warnings |
| Red-600 | #DC2626 | `--color-feedback-error` | Error states, destructive |
| Blue-600 | #2563EB | `--color-feedback-info` | Info banners |
| Purple-600 | #7C3AED | — | MCP / AI indicator badges |
| Purple-100 | #EDE9FE | `--color-bg-ai` | AI insight card backgrounds |

### 3.2 Colour Contrast Requirements (WCAG 2.1 AA)

Note: muted/placeholder text (3.1:1) is acceptable only for non-essential placeholder text. All meaningful text must pass AA minimum.

| Pair | Foreground | Background | Contrast Ratio | WCAG Level |
|------|-----------|-----------|----------------|------------|
| Primary text on canvas | #0F172A | #FAFAF9 | 18.5:1 | AAA |
| Primary button text | #FFFFFF | #16A34A | 5.1:1 | AA |
| Secondary text on surface | #475569 | #FFFFFF | 5.9:1 | AA |
| Muted text on canvas | #94A3B8 | #FAFAF9 | 3.1:1 | AA (large text only) |
| Error text on white | #DC2626 | #FFFFFF | 5.9:1 | AA |
| AI badge text on purple-100 | #5B21B6 | #EDE9FE | 5.3:1 | AA |
| Brand text on green-50 | #15803D | #F0FDF4 | 5.2:1 | AA |

---

## 4. Typography

### 4.1 Type Scale

Reflect uses two typefaces: DM Sans for UI text (clean, geometric, excellent at small sizes) and Fraunces for display headings (distinctive serif with character — reinforces the "this is a ritual, not a tool" feeling). Both are served via Google Fonts.

| Token | Family | Size | Weight | Line Height | Usage |
|-------|--------|------|--------|-------------|-------|
| `--type-display-xl` | Fraunces | 48px / 3rem | 700 | 1.1 | Landing page headline only |
| `--type-display-lg` | Fraunces | 36px / 2.25rem | 700 | 1.2 | Page-level titles (h1) |
| `--type-display-md` | Fraunces | 28px / 1.75rem | 600 | 1.25 | Section headings (h2) |
| `--type-heading-lg` | DM Sans | 22px / 1.375rem | 600 | 1.3 | Card titles, modal headings (h3) |
| `--type-heading-md` | DM Sans | 18px / 1.125rem | 600 | 1.4 | Sub-headings, form section labels |
| `--type-body-lg` | DM Sans | 16px / 1rem | 400 | 1.6 | Default body text |
| `--type-body-md` | DM Sans | 14px / 0.875rem | 400 | 1.6 | Secondary body, table content |
| `--type-body-sm` | DM Sans | 12px / 0.75rem | 400 | 1.5 | Labels, captions, helper text |
| `--type-mono` | DM Mono | 13px / 0.8125rem | 400 | 1.5 | Code, timestamps, token names |

### 4.2 Typography Rules

| Do | Don't |
|----|-------|
| Use Fraunces for headings that set the emotional tone of a page (page titles, check-in confirmation headline) | Mix more than two typefaces in a single view |
| Use DM Sans for all UI chrome: labels, buttons, nav, form fields, tables | Use Fraunces for body text or UI labels — its display nature makes it unreadable at small sizes |
| Set body text at 16px minimum; never go below 12px for any visible text | Set font sizes below 12px — WCAG requires readable text for all meaningful content |
| Use font-weight 600 for headings; 400 for body; never 300 (too light on low-res screens) | Use letter-spacing on body text — reserve it for monospace tokens and all-caps labels only |
| Set line-height 1.5-1.6 for body text; 1.1-1.3 for display headings | Use italic for emphasis in UI strings — use font-weight 600 instead; italic is for editorial content |

---

## 5. Spacing & Layout

### 5.1 Spacing Scale

All spacing uses a 4px base grid. Use only the defined spacing tokens — never hardcode arbitrary pixel values.

| Token | Value | Usage |
|-------|-------|-------|
| `--spacing-xs` | 4px | Icon gap, tight inline spacing |
| `--spacing-sm` | 8px | Within a component (e.g. button icon + label gap) |
| `--spacing-md` | 16px | Component internal padding (default for inputs, cards) |
| `--spacing-lg` | 24px | Between components within a section |
| `--spacing-xl` | 32px | Section internal padding; card padding on desktop |
| `--spacing-2xl` | 48px | Between major sections |
| `--spacing-3xl` | 64px | Page-level top/bottom padding |

### 5.2 Layout Grid

| Breakpoint | Min Width | Columns | Gutter | Margin | Max Content Width |
|------------|-----------|---------|--------|--------|-------------------|
| Mobile | 0px | 4 | 16px | 16px | 100% |
| Tablet | 640px | 8 | 24px | 24px | 100% |
| Desktop | 1024px | 12 | 24px | 32px | 1280px |
| Wide | 1440px | 12 | 32px | auto | 1280px (centred) |

### 5.3 Page Templates

| Template | Columns (desktop) | Used for | Sidebar |
|----------|-------------------|----------|---------|
| Single column | 1 (max 640px centred) | Check-in form, onboarding, paywall | None |
| Content + sidebar | 8 + 4 | Dashboard, history view | Right: streak card + AI insight latest |
| Full width | 12 | Landing page hero, Year-in-Review card | None |
| Settings | 3 (nav) + 9 (content) | Settings, Integrations (MCP), Billing | Left: section nav |

---

## 6. Elevation & Shadow

Reflect uses a minimal, flat-leaning aesthetic. Shadows are used sparingly and only to establish hierarchy, not decoration.

| Token | CSS Value | Usage |
|-------|-----------|-------|
| `--shadow-none` | `none` | Default state (no elevation) |
| `--shadow-xs` | `0 1px 2px 0 rgba(0,0,0,0.05)` | Subtle card lift on hover |
| `--shadow-sm` | `0 1px 3px 0 rgba(0,0,0,0.10), 0 1px 2px -1px rgba(0,0,0,0.10)` | Cards, form inputs (resting) |
| `--shadow-md` | `0 4px 6px -1px rgba(0,0,0,0.10), 0 2px 4px -2px rgba(0,0,0,0.10)` | Dropdowns, popovers |
| `--shadow-lg` | `0 10px 15px -3px rgba(0,0,0,0.10), 0 4px 6px -4px rgba(0,0,0,0.10)` | Modals, drawer panels |
| `--shadow-focus` | `0 0 0 3px rgba(22,163,74,0.35)` | Focus ring for interactive elements (green) |

---

## 7. Motion & Animation

Motion in Reflect should feel calm and purposeful — not flashy. Animations should reinforce state changes, not distract from them. The check-in confirmation is the one place where motion is celebrated.

| Token | Value | Usage |
|-------|-------|-------|
| `--motion-duration-fast` | 100ms | Hover states, button press feedback |
| `--motion-duration-base` | 200ms | Default transitions (colour, border, shadow) |
| `--motion-duration-slow` | 350ms | Page transitions, modal open/close |
| `--motion-duration-xslow` | 500ms | Streak counter increment, check-in confirmation |
| `--motion-ease-default` | `cubic-bezier(0.4, 0, 0.2, 1)` | Standard ease (Material Design standard) |
| `--motion-ease-in` | `cubic-bezier(0.4, 0, 1, 1)` | Elements leaving the screen |
| `--motion-ease-out` | `cubic-bezier(0, 0, 0.2, 1)` | Elements entering the screen |
| `--motion-ease-spring` | `cubic-bezier(0.34, 1.56, 0.64, 1)` | Streak counter, celebration moments |

### Reduced Motion

```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

All animations must degrade gracefully when the user has requested reduced motion. State changes must still occur — only the transition is removed, not the end state.

---

## 8. Component Library

Components are documented with: token usage, variants, states, accessibility requirements, and Do/Don't guidance. All components are implemented as React functional components in Next.js 14.

### 8.1 Button

**Variants**

| Variant | Token Usage | When to use |
|---------|-------------|-------------|
| Primary | `--color-bg-brand` + `--color-text-inverse` | Single primary action per view. Submit check-in, Upgrade to Pro, Connect MCP. |
| Secondary | `--color-interactive-secondary` + `--color-text-primary` | Secondary actions alongside a Primary. Cancel, Edit, View History. |
| Ghost | transparent bg + `--color-text-primary` border | Tertiary actions. Settings links, less important navigation. |
| Destructive | `--primitive-red-600` bg + white text | Irreversible actions only. Delete account, Revoke API key. |
| AI / MCP | `--color-bg-ai` + `--primitive-purple-600` text | Actions that trigger AI generation or MCP operations. Generate Insight. |

**States**

| State | Visual Change |
|-------|---------------|
| Default | Base variant styles |
| Hover | Background darkens by one step (e.g. green-600 to green-700); `--motion-duration-fast` transition |
| Focus | `--shadow-focus` ring (3px green with 0.35 opacity); visible without hover |
| Active | Scale transform: 0.98; `--motion-duration-fast` |
| Disabled | opacity: 0.45; cursor: not-allowed; no pointer events; `aria-disabled=true` |
| Loading | Spinner icon replaces label; button width locked to prevent layout shift; `aria-busy=true` |

**Component Token Example**

```css
.btn-primary {
  --btn-bg:           var(--color-bg-brand);
  --btn-bg-hover:     var(--color-interactive-primary-hover);
  --btn-text:         var(--color-text-inverse);
  --btn-radius:       var(--radius-md);
  --btn-padding-y:    var(--spacing-sm);
  --btn-padding-x:    var(--spacing-lg);
  --btn-font:         var(--type-body-lg);
  --btn-font-weight:  600;

  background: var(--btn-bg);
  color:      var(--btn-text);
  border-radius: var(--btn-radius);
  padding: var(--btn-padding-y) var(--btn-padding-x);
  transition: background var(--motion-duration-fast) var(--motion-ease-default),
              box-shadow var(--motion-duration-fast) var(--motion-ease-default);
}
.btn-primary:hover  { background: var(--btn-bg-hover); }
.btn-primary:focus-visible { box-shadow: var(--shadow-focus); outline: none; }
.btn-primary:active { transform: scale(0.98); }
```

| Do | Don't |
|----|-------|
| Use a single Primary button per view | Stack multiple Primary buttons in a row |
| Include visible loading state during async operations | Disable a button without explaining why via helper text |
| Always include `aria-label` if button has icon only | Use colour alone to convey button purpose |

### 8.2 Text Input & Textarea

**States**

| State | Border Token | Background | Additional |
|-------|-------------|-----------|-----------|
| Default | `--color-border-default` (slate-200) | `--color-bg-muted` (slate-100) | |
| Focus | `--color-border-focus` (green-600) | `--color-bg-surface` (white) | `--shadow-focus` ring |
| Error | `--color-border-error` (red-600) | `--color-bg-destructive` | Error message below; `aria-describedby` to error id |
| Disabled | `--color-border-default` | `--color-bg-muted` | opacity: 0.5; cursor: not-allowed |
| Read-only | `--color-border-default` | `--color-bg-subtle` | No focus ring; `aria-readonly=true` |

**Textarea Component Token Example**

```css
.checkin-textarea {
  --textarea-border:        var(--color-border-default);
  --textarea-bg:            var(--color-bg-muted);
  --textarea-border-focus:  var(--color-border-focus);
  --textarea-radius:        var(--radius-md);
  --textarea-padding:       var(--spacing-md);
  --textarea-min-height:    120px;

  border: 1.5px solid var(--textarea-border);
  background: var(--textarea-bg);
  border-radius: var(--textarea-radius);
  padding: var(--textarea-padding);
  min-height: var(--textarea-min-height);
  resize: vertical;
  transition: border-color var(--motion-duration-base) var(--motion-ease-default),
              box-shadow var(--motion-duration-base) var(--motion-ease-default);
}
.checkin-textarea:focus {
  border-color: var(--textarea-border-focus);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-focus);
  outline: none;
}
```

### 8.3 Energy Slider (Signature Component)

The energy slider is Reflect's most distinctive UI element — it replaces a plain number input with a visual 1-10 scale. It must feel premium, be easily operable on mobile touch, and produce interpretable data for the AI engine.

| Specification | Value |
|--------------|-------|
| Range | 1 (Very Low) to 10 (Very High) |
| Step | 1 (integer only — ensures clean AI input data) |
| Default | 5 (neutral; pre-selected to reduce friction) |
| Track fill colour | Gradient: red-400 (1) to amber-400 (5) to green-500 (10) |
| Label display | Numeric value + descriptive label at selected position (e.g. "7 — Good") |
| Mobile target size | Thumb: minimum 44x44px (WCAG 2.5.5 AAA target size) |
| Keyboard | Left/Right arrow keys change value; Home = 1, End = 10 |
| ARIA | `role=slider`, `aria-valuemin=1`, `aria-valuemax=10`, `aria-valuenow={value}`, `aria-valuetext="7 — Good energy"` |
| Haptic feedback | `navigator.vibrate(10)` on value change (mobile only, if supported) |

**Energy Labels**

```javascript
const ENERGY_LABELS = {
  1: 'Very low',   2: 'Low',    3: 'Below average',
  4: 'Slightly low', 5: 'Neutral', 6: 'Slightly good',
  7: 'Good',       8: 'High',   9: 'Very high',   10: 'Excellent'
};

/* Track gradient */
--slider-track-gradient: linear-gradient(
  to right,
  #F87171 0%,    /* red-400 at 1 */
  #FBBF24 45%,   /* amber-400 at 5 */
  #22C55E 100%   /* green-500 at 10 */
);
```

### 8.4 Card

| Variant | Border | Background | Shadow | Usage |
|---------|--------|-----------|--------|-------|
| Default | `--color-border-default` | `--color-bg-surface` | `--shadow-sm` | Check-in history items, insight report cards |
| Brand | `--color-border-brand` | `--color-bg-brand-subtle` | `--shadow-sm` | Streak card, Pro upgrade prompt |
| AI | `--primitive-purple-600` | `--color-bg-ai` | `--shadow-sm` | AI insight cards, MCP status card |
| Muted | `--color-border-default` | `--color-bg-subtle` | `--shadow-none` | Secondary information, settings sections |
| Ghost | none | transparent | `--shadow-none` | Inline containers with no visual weight |

| Do | Don't |
|----|-------|
| Set `min-height: 0` — let content define the height | Nest cards more than one level deep |
| Include `padding: var(--spacing-xl)` on desktop cards | Use cards for navigation items — use List or Nav components instead |
| Use Card variant semantically — AI card for AI content, Brand card for conversion moments | Apply multiple border colours on a single card |

### 8.5 Badge & Tag

| Variant | Background | Text Colour | Usage |
|---------|-----------|-------------|-------|
| Success | `--primitive-green-100` | `--primitive-green-700` | Streak milestone, subscription active |
| Warning | `--primitive-amber-100` (#FEF3C7) | `--primitive-amber-700` (#B45309) | Check-in due reminder, trial expiry |
| Error | `--primitive-red-50` | `--primitive-red-700` | Payment failed, account issue |
| Info | #DBEAFE | #1D4ED8 | New feature callout, beta label |
| AI / MCP | `--color-bg-ai` | `--primitive-purple-600` | AI-generated content indicator, MCP tool label |
| Neutral | `--color-bg-muted` | `--color-text-secondary` | Plan tier (Free), status (Draft) |

### 8.6 Streak Counter (Signature Component)

The streak counter is a key retention mechanic. Its animation is the primary celebration moment in the product. It should feel rewarding without being excessive.

| Property | Specification |
|----------|--------------|
| Display | Large numeric value (`--type-display-lg`, Fraunces) + "week streak" label below |
| Icon | Flame icon (Lucide `Flame`) left of number; animated on streak increment |
| Animation trigger | On check-in confirmation: count increments from n-1 to n over 500ms using `--motion-ease-spring` |
| Milestone badges | Week 4, 12, 26, 52 trigger a full-screen celebration overlay (confetti + milestone badge) |
| Zero state | Shows "0 week streak" with a muted colour and gentle nudge copy: "Start your streak this Sunday" |
| Broken streak | Shows streak count in muted colour with "streak paused" badge; does not show flame icon |
| ARIA | `aria-live=polite` on the counter element; announces new count on change |

### 8.7 Navigation

| Context | Pattern | Items | Mobile behaviour |
|---------|---------|-------|------------------|
| Authenticated app | Top navigation bar (sticky) | Dashboard, Check-in (CTA), History, Insights, Settings | Bottom tab bar (5 tabs max) |
| Settings area | Left sidebar nav | Profile, Notifications, Billing, Integrations (MCP), Data & Privacy | Collapsible drawer from top |
| Landing / marketing | Top nav (non-sticky) | Features, Pricing, Log In, Get Started (Primary button) | Hamburger menu; full-screen overlay |
| Check-in form | No navigation (focused mode) | Back arrow only (with exit confirmation if form has content) | Same as desktop |

**Active state rule:** Navigation items use a left border indicator (3px solid `--color-interactive-primary`) + `--color-text-brand` text for the active state. Never use background colour alone to indicate active state — it must also include a non-colour indicator for WCAG 1.4.1.

---

## 9. Accessibility

### 9.1 Baseline Requirements

WCAG 2.1 AA is the minimum standard for all Reflect interfaces. The following requirements are non-negotiable and must pass before any release.

| WCAG Criterion | Requirement | Reflect Implementation |
|---------------|-------------|----------------------|
| 1.1.1 Non-text content | All images and icons have text alternatives | All icons use `aria-label` or adjacent visible text. Decorative images: `aria-hidden=true`. |
| 1.3.1 Info and relationships | Structure conveyed programmatically | Semantic HTML: `<main>`, `<nav>`, `<section>`, `<h1>`-`<h3>` used correctly. Form labels use `<label for=>`. |
| 1.4.1 Use of colour | Colour not the only means of conveying information | Active nav item: left border + colour. Error states: icon + colour + text. Streak status: icon + colour. |
| 1.4.3 Contrast (minimum) | Text: 4.5:1; Large text: 3:1 | See Section 3.2 contrast table. All passing AA minimum. |
| 1.4.4 Resize text | Text resizable to 200% without loss of content | rem-based font sizes. No fixed-height containers with `overflow: hidden` on text. |
| 1.4.11 Non-text contrast | UI components: 3:1 minimum | Form borders (slate-200 on slate-100): 1.4:1 — **FAILS**. Must upgrade to slate-400 (#94A3B8) for borders. **Action required.** |
| 2.1.1 Keyboard | All functionality available via keyboard | All interactive elements in natural tab order. Custom slider has keyboard handlers (arrow keys, Home, End). |
| 2.4.3 Focus order | Focus order matches visual order | DOM order matches visual order. No CSS position tricks that break tab sequence. |
| 2.4.7 Focus visible | Keyboard focus visible | `--shadow-focus` applied to all `:focus-visible` states. Never `outline: none` without replacement. |
| 4.1.2 Name, role, value | All UI components have accessible names and roles | All form inputs have associated `<label>`. Custom components use ARIA roles. Dynamic states use `aria-expanded`, `aria-checked`, `aria-busy`. |

> **Action Required: Non-text contrast (1.4.11)**
>
> The default input border (`--color-border-default`: #E2E8F0 on #F1F5F9 background) produces a contrast ratio of ~1.4:1, which fails WCAG 1.4.11 (3:1 required for UI components). Fix: Change `--color-border-default` for input contexts to #94A3B8 (slate-400) or #64748B (slate-500) for definitive compliance (3.6:1 on white).

### 9.2 Focus Management Rules

- On modal open: focus moves to the modal container (or first interactive element inside it)
- On modal close: focus returns to the element that triggered the modal
- On page navigation (Next.js router): focus moves to the page `<h1>`
- On check-in form submission: focus moves to the confirmation message heading
- On error: focus moves to the first error message (`aria-live=assertive`)

### 9.3 Screen Reader Testing Matrix

| Browser | Screen Reader | Platform | Coverage |
|---------|--------------|----------|----------|
| Chrome | NVDA (latest) | Windows | Primary — most common combination globally |
| Safari | VoiceOver | macOS | Required — important for iOS users (same engine) |
| Firefox | NVDA | Windows | Secondary — validate ARIA implementation |
| Safari | VoiceOver | iOS | Required — mobile check-in use case |
| Chrome | TalkBack | Android | Secondary — validate mobile form usability |

---

## 10. Dark Mode

Dark mode is a Phase 3 feature. The token architecture is designed to support it from day one via CSS custom property overrides on a `[data-theme='dark']` selector. No component-level changes are required when the token values are correctly updated.

```css
[data-theme='dark'] {
  --color-text-primary:    #F8FAFC;
  --color-text-secondary:  #94A3B8;
  --color-text-muted:      #64748B;
  --color-bg-canvas:       #0F172A;
  --color-bg-surface:      #1E293B;
  --color-bg-subtle:       #1E293B;
  --color-bg-muted:        #334155;
  --color-border-default:  #334155;
  --color-border-strong:   #475569;
  /* Brand greens remain unchanged — sufficient contrast on dark bg */
  /* Re-validate all contrast pairs in Section 3.2 before shipping dark mode */
}
```

---

## 11. Icon System

Reflect uses Lucide React as its icon library. Lucide is open-source, tree-shakeable, and SVG-based with consistent stroke widths (1.5px default). Custom icons are only introduced when Lucide does not have an appropriate match.

| Icon | Lucide Name | Usage in Reflect |
|------|------------|------------------|
| Checkmark | `Check` | Check-in completion confirmation |
| Flame | `Flame` | Streak counter (custom colour gradient) |
| Bolt / Zap | `Zap` | Energy rating indicator |
| Calendar | `Calendar` | Week dates, check-in history |
| Chart | `TrendingUp` | Dashboard trend charts |
| Insight | `Sparkles` | AI insight card indicator |
| Link / MCP | `Link2` | MCP integration status |
| Settings | `Settings` | Settings navigation |
| Billing | `CreditCard` | Billing section |
| Lock | `Lock` | Free-tier locked features |
| Arrow | `ArrowUp` / `ArrowDown` | Trend direction on charts |
| Close | `X` | Modal close, dismiss banner |

**Icon Sizes**

| Size Token | Value | Usage |
|-----------|-------|-------|
| `--icon-xs` | 12px | Inline with small body text |
| `--icon-sm` | 16px | Inline with default body text, badge icons |
| `--icon-md` | 20px | Button icons, navigation items |
| `--icon-lg` | 24px | Feature card icons, section headers |
| `--icon-xl` | 32px | Confirmation screens, empty states |
| `--icon-2xl` | 48px | Milestone celebration overlays |

---

## 12. Content & Voice Guidelines

### 12.1 Brand Voice

| Attribute | Description | Example |
|-----------|-------------|---------|
| Direct | No waffle. Say the thing. | Good: "Log this week" / Bad: "Why not take a moment to capture your week's highlights?" |
| Warm | Human, not robotic. Personal, not generic. | Good: "Nice work — 12 weeks straight." / Bad: "Streak milestone achieved." |
| Honest | AI insights acknowledge uncertainty. | Good: "This pattern suggests..." / Bad: "You definitely perform better when..." |
| Calm | No urgency theatre. No FOMO copy. | Good: "Your monthly insight is ready." / Bad: "Don't miss your insight! Act now!" |
| Brief | If in doubt, remove words. | Good: "Wins" / Bad: "What were your wins this week?" |

### 12.2 Check-In Prompt Copy

The 5 prompts are the product's most important copy. They have been designed to feel like a coaching conversation, not a form.

| Prompt # | Label | Placeholder text | Section |
|----------|-------|-----------------|---------|
| 1 | Wins | What went well this week? Big or small. | Reflection |
| 2 | Blockers | What got in your way, or is still unresolved? | Reflection |
| 3 | Energy | How was your energy? Slide to rate, then add any context below. | Energy |
| 4 | Energy context | What drove that energy level? (Optional) | Energy |
| 5 | Intentions | What matters most next week? | Intentions |

| Do | Don't |
|----|-------|
| Keep prompts to one idea each — no compound questions | Use formal or clinical language ("Please describe your achievements") |
| Use second-person voice consistently ("What went well for you") | Add helper text that's longer than the prompt itself |
| Mark optional fields explicitly with "(Optional)" in the placeholder | Use the word "journal" — it triggers connotations of inconsistency |
| Acknowledge completion warmly: "Done. Week 14 logged." | Use exclamation marks in UI copy — they feel forced in a reflective context |

---

## Appendix

### A. Token Quick Reference

| Category | Token | Value |
|----------|-------|-------|
| Text | `--color-text-primary` | #0F172A |
| Text | `--color-text-secondary` | #475569 |
| Text | `--color-text-muted` | #94A3B8 |
| Text | `--color-text-brand` | #15803D |
| Text | `--color-text-destructive` | #DC2626 |
| BG | `--color-bg-canvas` | #FAFAF9 |
| BG | `--color-bg-surface` | #FFFFFF |
| BG | `--color-bg-subtle` | #F8FAFC |
| BG | `--color-bg-brand` | #22C55E |
| BG | `--color-bg-brand-subtle` | #F0FDF4 |
| BG | `--color-bg-ai` | #EDE9FE |
| Border | `--color-border-default` | #E2E8F0 |
| Border | `--color-border-focus` | #16A34A |
| Border | `--color-border-error` | #DC2626 |
| Spacing | `--spacing-md` | 16px |
| Spacing | `--spacing-lg` | 24px |
| Spacing | `--spacing-xl` | 32px |
| Radius | `--radius-md` | 8px |
| Radius | `--radius-lg` | 12px |
| Shadow | `--shadow-focus` | `0 0 0 3px rgba(22,163,74,0.35)` |
| Motion | `--motion-duration-base` | 200ms |
| Motion | `--motion-ease-default` | `cubic-bezier(0.4,0,0.2,1)` |

### B. Font Loading (Next.js)

```tsx
// app/layout.tsx
import { DM_Sans, DM_Mono, Fraunces } from 'next/font/google';

const dmSans = DM_Sans({
  subsets: ['latin'],
  variable: '--font-dm-sans',
  display: 'swap',
});

const dmMono = DM_Mono({
  subsets: ['latin'],
  weight: ['400', '500'],
  variable: '--font-dm-mono',
  display: 'swap',
});

const fraunces = Fraunces({
  subsets: ['latin'],
  variable: '--font-fraunces',
  display: 'swap',
  axes: ['SOFT', 'WONK'],  // variable font axes for optical sizing
});

// Apply to <html> tag:
// className={`${dmSans.variable} ${dmMono.variable} ${fraunces.variable}`}
```

### C. Glossary

| Term | Definition |
|------|-----------|
| Design Token | A named variable storing a single visual design value (colour, spacing, radius, etc.) |
| Primitive token | Tier 1 token. Raw value with no semantic meaning. Never used directly in component styles. |
| Semantic token | Tier 2 token. Named by purpose (e.g. `--color-text-primary`). What components reference. |
| Component token | Tier 3 token. Context-specific alias scoped to one component. |
| WCAG | Web Content Accessibility Guidelines. AA is the minimum standard for Reflect. |
| ARIA | Accessible Rich Internet Applications — HTML attributes that expose UI state to assistive technologies. |
| DM Sans | Primary UI typeface — geometric sans-serif with strong legibility at small sizes. |
| Fraunces | Display/heading typeface — distinctive variable serif with emotional resonance. |
| Lucide | Open-source icon library used throughout Reflect (SVG, React component-based, tree-shakeable). |
| Energy slider | Reflect's signature input component for the `energy_rating` check-in field (1-10 range). |
| Streak counter | Reflect's signature retention component showing consecutive weeks of check-in completion. |
