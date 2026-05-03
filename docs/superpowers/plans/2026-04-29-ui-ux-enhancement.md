# UI/UX Enhancement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Transform the Reflect frontend from its current indigo/Inter MVP styling to a calm, minimal design system aligned with the published spec — emerald palette, DM Sans + Fraunces typography, 21st.dev-inspired interactions.

**Architecture:** Pure frontend changes across 7 phases. Phase 0 (Tailwind config + fonts + globals) cascades to all pages. Phases 1–6 enhance individual areas in priority order. No backend, no API changes, no new routes.

**Tech Stack:** Next.js 14, React 18, Tailwind CSS 3.4, Ark UI 5.36, @headlessui/react (new — Transition only), Lucide React (new — icons).

**Spec:** `docs/superpowers/specs/2026-04-29-ui-ux-enhancement-design.md`

---

## File Structure

### New Files

| File | Responsibility |
|------|---------------|
| `web/src/components/StepDots.tsx` | Connected-dot progress indicator for the check-in wizard |
| `web/src/components/CompletionMoment.tsx` | SVG checkmark animation + streak summary after wizard submission |
| `web/src/components/NumberTicker.tsx` | Animated number counter (requestAnimationFrame, no deps) |
| `web/src/components/Skeleton.tsx` | Shimmer skeleton loading component |
| `web/src/components/EmptyState.tsx` | Reusable empty state with icon, heading, description, CTA |
| `web/src/components/BottomNav.tsx` | Mobile bottom tab navigation |
| `web/src/components/AuthLayout.tsx` | Shared split layout for all auth pages |
| `web/src/components/FloatingInput.tsx` | Floating label input (CSS-only via peer selectors) |
| `web/src/hooks/useScrolled.ts` | Hook that returns true when page scrolled past threshold |

### Modified Files

| File | What Changes |
|------|-------------|
| `web/tailwind.config.ts` | Emerald palette, semantic tokens, font families, keyframes, animations |
| `web/src/app/layout.tsx` | Load DM Sans + Fraunces + DM Mono fonts, apply CSS vars to body |
| `web/src/app/globals.css` | Body background `bg-canvas`, SVG checkmark keyframes |
| `web/package.json` | Add `@headlessui/react`, `lucide-react` |
| `web/src/components/CheckInWizard.tsx` | Card layout, StepDots, slide transitions, Fraunces prompts, CompletionMoment |
| `web/src/components/EnergySlider.tsx` | Update colors from indigo/gray to emerald/slate semantic tokens |
| `web/src/components/Header.tsx` | Frosted glass effect, streak badge, hide nav on mobile |
| `web/src/components/StreakBadge.tsx` | Remove flame SVGs, use dot icon + NumberTicker + milestone badges |
| `web/src/components/CheckInCard.tsx` | Timeline card styling with energy-colored dot |
| `web/src/components/CheckInDetail.tsx` | Update to semantic colors/typography |
| `web/src/app/history/page.tsx` | Vertical timeline layout, staggered animations, empty state, skeleton loading |
| `web/src/components/MonthlyInsightCard.tsx` | Purple AI accent, updated typography |
| `web/src/components/InsightSection.tsx` | Purple accent, semantic colors |
| `web/src/app/account/page.tsx` | Streak hero, sectioned cards, semantic colors |
| `web/src/app/login/page.tsx` | AuthLayout wrapper, floating labels, updated copy |
| `web/src/app/register/page.tsx` | AuthLayout wrapper, floating labels, updated copy |
| `web/src/app/forgot-password/page.tsx` | AuthLayout wrapper, floating labels, updated copy |
| `web/src/app/reset-password/page.tsx` | AuthLayout wrapper, floating labels, updated copy |
| `web/src/components/PricingCards.tsx` | Update to semantic colors |
| `web/src/components/UpgradePrompt.tsx` | Update to semantic colors |
| `web/src/components/Spinner.tsx` | Update from `text-primary-600` to `text-primary-400` |
| `web/src/components/VerificationBanner.tsx` | Update to semantic colors |

---

## Task 1: Install Dependencies

**Files:**
- Modify: `web/package.json`

- [ ] **Step 1: Install @headlessui/react and lucide-react**

```bash
cd web && npm install @headlessui/react lucide-react
```

Expected: packages added to `dependencies` in `package.json`.

- [ ] **Step 2: Verify install**

```bash
cd web && npm ls @headlessui/react lucide-react
```

Expected: both packages listed without errors.

- [ ] **Step 3: Commit**

```bash
git add web/package.json web/package-lock.json
git commit -m "chore: add @headlessui/react and lucide-react dependencies"
```

---

## Task 2: Update Tailwind Config — Emerald Palette, Semantic Tokens, Fonts, Animations

**Files:**
- Modify: `web/tailwind.config.ts`

- [ ] **Step 1: Replace the entire Tailwind config**

Replace the full contents of `web/tailwind.config.ts` with:

```typescript
import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: "#ECFDF5",
          100: "#D1FAE5",
          200: "#A7F3D0",
          300: "#6EE7B7",
          400: "#34D399",
          500: "#10B981",
          600: "#059669",
          700: "#047857",
          800: "#065F46",
          900: "#064E3B",
        },
        canvas: "#FAFAF9",
        surface: "#FFFFFF",
        "text-primary": "#0F172A",
        "text-secondary": "#475569",
        "text-muted": "#94A3B8",
        "border-default": "#E2E8F0",
        "border-input": "#94A3B8",
        accent: {
          amber: "#F59E0B",
          red: "#DC2626",
          purple: "#7C3AED",
        },
      },
      fontFamily: {
        sans: ["var(--font-sans)", "system-ui", "sans-serif"],
        serif: ["var(--font-serif)", "Georgia", "serif"],
        mono: ["var(--font-mono)", "Courier", "monospace"],
      },
      borderRadius: {
        card: "12px",
        input: "10px",
        badge: "8px",
      },
      keyframes: {
        "slide-in-right": {
          "0%": { transform: "translateX(30px)", opacity: "0" },
          "100%": { transform: "translateX(0)", opacity: "1" },
        },
        "slide-in-left": {
          "0%": { transform: "translateX(-30px)", opacity: "0" },
          "100%": { transform: "translateX(0)", opacity: "1" },
        },
        "slide-out-left": {
          "0%": { transform: "translateX(0)", opacity: "1" },
          "100%": { transform: "translateX(-30px)", opacity: "0" },
        },
        "slide-out-right": {
          "0%": { transform: "translateX(0)", opacity: "1" },
          "100%": { transform: "translateX(30px)", opacity: "0" },
        },
        "fade-in": {
          "0%": { opacity: "0" },
          "100%": { opacity: "1" },
        },
        shimmer: {
          "0%": { backgroundPosition: "-200% 0" },
          "100%": { backgroundPosition: "200% 0" },
        },
      },
      animation: {
        "step-enter-right": "slide-in-right 300ms ease-out",
        "step-enter-left": "slide-in-left 300ms ease-out",
        "step-exit-left": "slide-out-left 200ms ease-in",
        "step-exit-right": "slide-out-right 200ms ease-in",
        "fade-in": "fade-in 300ms ease-out",
        shimmer: "shimmer 1.5s ease-in-out infinite",
      },
    },
  },
  plugins: [],
};

export default config;
```

- [ ] **Step 2: Verify Tailwind compiles**

```bash
cd web && npx tailwindcss --content './src/**/*.tsx' --output /dev/null
```

Expected: no errors.

- [ ] **Step 3: Commit**

```bash
git add web/tailwind.config.ts
git commit -m "feat: update Tailwind config with emerald palette, semantic tokens, fonts, and animations"
```

---

## Task 3: Load Fonts and Update Global Styles

**Files:**
- Modify: `web/src/app/layout.tsx`
- Modify: `web/src/app/globals.css`

- [ ] **Step 1: Replace layout.tsx with new font loading**

Replace the full contents of `web/src/app/layout.tsx`:

```typescript
import type { Metadata } from "next";
import { DM_Sans, DM_Mono } from "next/font/google";
import localFont from "next/font/local";
import { AuthProvider } from "@/lib/auth";
import Header from "@/components/Header";
import VerificationBanner from "@/components/VerificationBanner";
import "./globals.css";

const dmSans = DM_Sans({
  subsets: ["latin"],
  weight: ["400", "500", "600"],
  variable: "--font-sans",
  display: "swap",
});

const fraunces = localFont({
  src: [
    {
      path: "../fonts/Fraunces-Regular.woff2",
      weight: "400",
      style: "normal",
    },
    {
      path: "../fonts/Fraunces-SemiBold.woff2",
      weight: "600",
      style: "normal",
    },
  ],
  variable: "--font-serif",
  display: "swap",
});

const dmMono = DM_Mono({
  subsets: ["latin"],
  weight: ["400"],
  variable: "--font-mono",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Reflect",
  description: "Guided weekly review for working professionals",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${dmSans.variable} ${fraunces.variable} ${dmMono.variable}`}>
      <body className="font-sans bg-canvas text-text-primary antialiased">
        <AuthProvider>
          <Header />
          <VerificationBanner />
          <main>{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
```

**Important:** Fraunces is loaded as a local font because `next/font/google` does not reliably load Fraunces variable font weights. Download the font files first:

```bash
mkdir -p web/src/fonts
curl -L "https://fonts.gstatic.com/s/fraunces/v31/6NUh8FyLNQOQZAnv9bYEvDiIdE9Ea92uemAk_WBq8U_9v0c2Wa0K7iN7hzFUPJH58nk.woff2" -o web/src/fonts/Fraunces-Regular.woff2
curl -L "https://fonts.gstatic.com/s/fraunces/v31/6NUh8FyLNQOQZAnv9bYEvDiIdE9Ea92uemAk_WBq8U_9v0c2Wa0K7iN7hzFUPJH58nk.woff2" -o web/src/fonts/Fraunces-SemiBold.woff2
```

**Note:** If the local font download fails or the URLs are stale, fall back to `next/font/google`:

```typescript
import { Fraunces } from "next/font/google";

const fraunces = Fraunces({
  subsets: ["latin"],
  weight: ["400", "600"],
  variable: "--font-serif",
  display: "swap",
});
```

Test both approaches and use whichever loads correctly.

- [ ] **Step 2: Update globals.css**

Replace the full contents of `web/src/app/globals.css`:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  body {
    @apply bg-canvas text-text-primary antialiased;
  }
}

@layer utilities {
  /* SVG checkmark draw animation for completion moment */
  .animate-draw-circle {
    stroke-dasharray: 166;
    stroke-dashoffset: 166;
    animation: draw-circle 0.6s ease-out forwards;
  }

  .animate-draw-check {
    stroke-dasharray: 48;
    stroke-dashoffset: 48;
    animation: draw-check 0.3s ease-out 0.6s forwards;
  }
}

@keyframes draw-circle {
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes draw-check {
  100% {
    stroke-dashoffset: 0;
  }
}
```

- [ ] **Step 3: Verify the app compiles and renders**

```bash
cd web && npm run build
```

Expected: build succeeds. If Fraunces font files are missing, the build will still succeed but the font won't render — check the console for font loading warnings and fix the font source.

- [ ] **Step 4: Commit**

```bash
git add web/src/app/layout.tsx web/src/app/globals.css web/src/fonts/
git commit -m "feat: load DM Sans, Fraunces, DM Mono fonts and update global styles to canvas background"
```

---

## Task 4: Create Reusable Utility Components

**Files:**
- Create: `web/src/components/NumberTicker.tsx`
- Create: `web/src/components/Skeleton.tsx`
- Create: `web/src/components/EmptyState.tsx`
- Create: `web/src/hooks/useScrolled.ts`

- [ ] **Step 1: Create NumberTicker.tsx**

Create `web/src/components/NumberTicker.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";

interface NumberTickerProps {
  value: number;
  duration?: number;
  className?: string;
}

export default function NumberTicker({ value, duration = 500, className }: NumberTickerProps) {
  const [display, setDisplay] = useState(0);

  useEffect(() => {
    const startTime = performance.now();
    let rafId: number;

    function tick(now: number) {
      const progress = Math.min((now - startTime) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      setDisplay(Math.round(eased * value));
      if (progress < 1) {
        rafId = requestAnimationFrame(tick);
      }
    }

    rafId = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(rafId);
  }, [value, duration]);

  return <span className={className}>{display}</span>;
}
```

- [ ] **Step 2: Create Skeleton.tsx**

Create `web/src/components/Skeleton.tsx`:

```typescript
interface SkeletonProps {
  className?: string;
}

export default function Skeleton({ className }: SkeletonProps) {
  return (
    <div
      className={`animate-shimmer rounded-card bg-gradient-to-r from-border-default via-slate-100 to-border-default bg-[length:200%_100%] ${className ?? ""}`}
    />
  );
}
```

- [ ] **Step 3: Create EmptyState.tsx**

Create `web/src/components/EmptyState.tsx`:

```typescript
import type { ReactNode } from "react";

interface EmptyStateProps {
  icon: ReactNode;
  heading: string;
  description: string;
  action?: ReactNode;
}

export default function EmptyState({ icon, heading, description, action }: EmptyStateProps) {
  return (
    <div className="flex min-h-[300px] flex-col items-center justify-center gap-3 rounded-xl border border-dashed border-border-default p-8 text-center">
      <div className="rounded-full bg-slate-100 p-4 text-text-muted">
        {icon}
      </div>
      <h3 className="font-medium text-text-primary">{heading}</h3>
      <p className="max-w-sm text-sm text-text-secondary">{description}</p>
      {action}
    </div>
  );
}
```

- [ ] **Step 4: Create useScrolled.ts**

Create `web/src/hooks/useScrolled.ts`:

```typescript
"use client";

import { useEffect, useState } from "react";

export function useScrolled(threshold = 10): boolean {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    function handler() {
      setScrolled(window.scrollY > threshold);
    }

    handler();
    window.addEventListener("scroll", handler, { passive: true });
    return () => window.removeEventListener("scroll", handler);
  }, [threshold]);

  return scrolled;
}
```

- [ ] **Step 5: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 6: Commit**

```bash
git add web/src/components/NumberTicker.tsx web/src/components/Skeleton.tsx web/src/components/EmptyState.tsx web/src/hooks/useScrolled.ts
git commit -m "feat: add NumberTicker, Skeleton, EmptyState components and useScrolled hook"
```

---

## Task 5: Update Spinner to Semantic Colors

**Files:**
- Modify: `web/src/components/Spinner.tsx`

- [ ] **Step 1: Update Spinner color**

In `web/src/components/Spinner.tsx`, replace `text-primary-600` with `text-primary-400`:

```typescript
interface SpinnerProps {
  label?: string;
}

export default function Spinner({ label }: SpinnerProps) {
  return (
    <div className="flex items-center justify-center gap-3 py-8">
      <svg
        className="h-5 w-5 animate-spin text-primary-400"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <circle
          className="opacity-25"
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          strokeWidth="4"
        />
        <path
          className="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        />
      </svg>
      {label && <span className="text-sm text-text-secondary">{label}</span>}
    </div>
  );
}
```

- [ ] **Step 2: Commit**

```bash
git add web/src/components/Spinner.tsx
git commit -m "refactor: update Spinner to emerald semantic colors"
```

---

## Task 6: Enhance Header — Frosted Glass + Streak Badge + Mobile Bottom Nav

**Files:**
- Modify: `web/src/components/Header.tsx`
- Create: `web/src/components/BottomNav.tsx`

- [ ] **Step 1: Create BottomNav.tsx**

Create `web/src/components/BottomNav.tsx`:

```typescript
"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { PenLine, Clock, Sparkles, User } from "lucide-react";

const tabs = [
  { href: "/check-in", label: "Check-in", icon: PenLine },
  { href: "/history", label: "History", icon: Clock },
  { href: "/insights", label: "Insights", icon: Sparkles },
  { href: "/account", label: "Account", icon: User },
] as const;

export default function BottomNav() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 border-t border-border-default bg-surface/90 backdrop-blur-sm pb-[env(safe-area-inset-bottom)] md:hidden">
      <div className="flex items-center justify-around py-2">
        {tabs.map(({ href, label, icon: Icon }) => {
          const active = pathname.startsWith(href);
          return (
            <Link
              key={href}
              href={href}
              className={`flex flex-col items-center gap-0.5 px-3 py-1 text-[11px] ${
                active
                  ? "text-primary-400 border-t-2 border-primary-400 -mt-[2px]"
                  : "text-text-muted"
              }`}
            >
              <Icon size={20} strokeWidth={active ? 2 : 1.5} />
              <span>{label}</span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
```

- [ ] **Step 2: Rewrite Header.tsx with frosted glass and streak badge**

Replace the full contents of `web/src/components/Header.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { useScrolled } from "@/hooks/useScrolled";
import { apiFetch } from "@/lib/api";

export default function Header() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const scrolled = useScrolled(10);
  const [streak, setStreak] = useState<number | null>(null);

  useEffect(() => {
    if (!user) return;
    apiFetch<{ streak: number }>("/api/check-ins/streak")
      .then((data) => setStreak(data.streak))
      .catch(() => setStreak(null));
  }, [user]);

  async function handleLogout() {
    await logout();
    router.push("/login");
  }

  return (
    <header
      className={`sticky top-0 z-50 w-full transition-all duration-300 ${
        scrolled
          ? "bg-canvas/80 backdrop-blur-md shadow-sm border-b border-border-default"
          : "bg-transparent"
      }`}
    >
      <div className="mx-auto flex h-14 max-w-3xl items-center justify-between px-4">
        <Link href={user ? "/check-in" : "/"} className="font-serif text-lg font-semibold text-text-primary">
          Reflect
        </Link>

        {user && (
          <nav className="hidden items-center gap-5 md:flex">
            <Link href="/check-in" className="text-sm text-text-secondary hover:text-text-primary">
              Check-in
            </Link>
            <Link href="/history" className="text-sm text-text-secondary hover:text-text-primary">
              History
            </Link>
            <Link href="/account" className="flex items-center gap-2 text-sm text-text-secondary hover:text-text-primary">
              {user.displayName}
              {user.pro && (
                <span className="rounded-badge bg-primary-100 px-1.5 py-0.5 text-[10px] font-semibold uppercase tracking-wide text-primary-700">
                  Pro
                </span>
              )}
            </Link>
            {streak !== null && streak > 0 && (
              <span className="flex items-center gap-1.5 rounded-badge bg-amber-50 px-2.5 py-0.5 text-xs font-semibold text-amber-800">
                <span className="inline-block h-2 w-2 rounded-full bg-primary-400" />
                {streak}
              </span>
            )}
            <button onClick={handleLogout} className="text-sm text-text-secondary hover:text-text-primary">
              Sign out
            </button>
          </nav>
        )}
      </div>
    </header>
  );
}
```

- [ ] **Step 3: Add BottomNav to root layout**

In `web/src/app/layout.tsx`, import and render `BottomNav` below `<main>`, inside `AuthProvider`:

Add the import at the top:

```typescript
import BottomNav from "@/components/BottomNav";
```

Then in the JSX, after `<main>{children}</main>`, add:

```typescript
<BottomNav />
```

Also add bottom padding to `<main>` for mobile so content isn't hidden behind the bottom nav:

```typescript
<main className="pb-16 md:pb-0">{children}</main>
```

- [ ] **Step 4: Verify build and test**

```bash
cd web && npm run build
```

Expected: build succeeds. Visually: header should be transparent initially, gain frosted-glass effect on scroll. Streak badge shows as `● {number}` in amber pill. Bottom nav visible on mobile widths, hidden on desktop.

- [ ] **Step 5: Commit**

```bash
git add web/src/components/Header.tsx web/src/components/BottomNav.tsx web/src/app/layout.tsx
git commit -m "feat: frosted glass header, streak badge, mobile bottom nav"
```

---

## Task 7: Create StepDots and CompletionMoment Components

**Files:**
- Create: `web/src/components/StepDots.tsx`
- Create: `web/src/components/CompletionMoment.tsx`

- [ ] **Step 1: Create StepDots.tsx**

Create `web/src/components/StepDots.tsx`:

```typescript
import { Check } from "lucide-react";

interface StepDotsProps {
  total: number;
  current: number;
}

export default function StepDots({ total, current }: StepDotsProps) {
  return (
    <div className="flex items-center justify-center gap-1.5">
      {Array.from({ length: total }, (_, i) => (
        <div key={i} className="flex items-center gap-1.5">
          <div
            className={`flex items-center justify-center rounded-full transition-colors duration-300 ${
              i < current
                ? "h-2.5 w-2.5 bg-primary-400 md:h-3 md:w-3"
                : i === current
                  ? "h-2.5 w-2.5 bg-primary-400/20 ring-2 ring-primary-400 md:h-3 md:w-3"
                  : "h-2.5 w-2.5 bg-border-default md:h-3 md:w-3"
            }`}
          >
            {i < current && <Check size={8} strokeWidth={3} className="text-white hidden md:block" />}
          </div>
          {i < total - 1 && (
            <div
              className={`h-0.5 w-4 rounded-full transition-colors duration-500 md:w-6 ${
                i < current ? "bg-primary-400" : "bg-border-default"
              }`}
            />
          )}
        </div>
      ))}
    </div>
  );
}
```

- [ ] **Step 2: Create CompletionMoment.tsx**

Create `web/src/components/CompletionMoment.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import NumberTicker from "./NumberTicker";

interface CompletionMomentProps {
  checkInId: string;
  streak: number;
}

export default function CompletionMoment({ checkInId, streak }: CompletionMomentProps) {
  const router = useRouter();
  const [showSummary, setShowSummary] = useState(false);

  useEffect(() => {
    const summaryTimer = setTimeout(() => setShowSummary(true), 1000);
    const redirectTimer = setTimeout(() => router.push(`/history/${checkInId}`), 3000);
    return () => {
      clearTimeout(summaryTimer);
      clearTimeout(redirectTimer);
    };
  }, [checkInId, router]);

  return (
    <div className="flex min-h-[40vh] flex-col items-center justify-center gap-6 transition-colors duration-300 bg-primary-50/50 rounded-card p-8">
      {/* Animated SVG checkmark */}
      <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle
          cx="32"
          cy="32"
          r="26.4"
          stroke="#34D399"
          strokeWidth="3"
          fill="none"
          className="animate-draw-circle"
        />
        <path
          d="M20 32 L28 40 L44 24"
          stroke="#34D399"
          strokeWidth="3"
          strokeLinecap="round"
          strokeLinejoin="round"
          fill="none"
          className="animate-draw-check"
        />
      </svg>

      {showSummary && (
        <div className="animate-fade-in text-center">
          <p className="text-lg font-medium text-text-primary">Check-in complete.</p>
          {streak > 0 && (
            <p className="mt-1 text-sm text-text-secondary">
              Week <NumberTicker value={streak} duration={500} className="font-semibold tabular-nums text-text-primary" />.
            </p>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 3: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 4: Commit**

```bash
git add web/src/components/StepDots.tsx web/src/components/CompletionMoment.tsx
git commit -m "feat: add StepDots progress indicator and CompletionMoment animation"
```

---

## Task 8: Enhance Check-in Wizard — Card Layout, Transitions, Typography

**Files:**
- Modify: `web/src/components/CheckInWizard.tsx`
- Modify: `web/src/components/EnergySlider.tsx`

- [ ] **Step 1: Update EnergySlider.tsx to semantic colors**

Replace the full contents of `web/src/components/EnergySlider.tsx`:

```typescript
"use client";

import { Slider } from "@ark-ui/react/slider";

interface EnergySliderProps {
  value: number;
  onChange: (value: number) => void;
}

const energyLabels: Record<number, string> = {
  1: "Exhausted", 2: "Very low", 3: "Low", 4: "Below average", 5: "Neutral",
  6: "Above average", 7: "Good", 8: "High", 9: "Very high", 10: "Peak",
};

function energyColor(value: number): string {
  if (value <= 3) return "bg-red-500";
  if (value <= 5) return "bg-amber-500";
  if (value <= 7) return "bg-primary-400";
  return "bg-primary-500";
}

export default function EnergySlider({ value, onChange }: EnergySliderProps) {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <span className="font-serif text-4xl font-semibold text-text-primary">{value}</span>
        <span className="text-sm text-text-secondary">{energyLabels[value]}</span>
      </div>
      <Slider.Root min={1} max={10} step={1} value={[value]} onValueChange={(details) => onChange(details.value[0])}>
        <Slider.Control className="relative flex h-6 items-center">
          <Slider.Track className="relative h-2 w-full rounded-full bg-border-default">
            <Slider.Range className={`absolute h-full rounded-full ${energyColor(value)}`} />
          </Slider.Track>
          <Slider.Thumb
            index={0}
            className="block h-5 w-5 rounded-full border-2 border-primary-400 bg-white shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 cursor-grab active:cursor-grabbing"
          />
        </Slider.Control>
      </Slider.Root>
      <div className="flex justify-between text-xs text-text-muted">
        <span>Very low</span>
        <span>Excellent</span>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Rewrite CheckInWizard.tsx**

Replace the full contents of `web/src/components/CheckInWizard.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Transition } from "@headlessui/react";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, CheckInRequest } from "@/lib/types";
import EnergySlider from "./EnergySlider";
import StepDots from "./StepDots";
import CompletionMoment from "./CompletionMoment";

interface WizardProps {
  existing?: CheckInResponse | null;
}

const STEPS = [
  { key: "wins", label: "Progress", prompt: "What moved forward this week?", subtitle: "In your work or how you worked with others" },
  { key: "friction", label: "Friction", prompt: "Where did you feel resistance?", subtitle: "A task, a person, or yourself" },
  { key: "energyRating", label: "Energy", prompt: "How was your energy this week?", subtitle: "Rate from 1 (exhausted) to 10 (peak)" },
  { key: "signalMoment", label: "Signal Moment", prompt: "What interaction is still on your mind?", subtitle: "And why does it linger?" },
  { key: "intentions", label: "Intentions", prompt: "What matters most next week?", subtitle: "And why does it matter?" },
] as const;

export default function CheckInWizard({ existing }: WizardProps) {
  const router = useRouter();
  const [step, setStep] = useState(0);
  const [direction, setDirection] = useState<"forward" | "backward">("forward");
  const [showStep, setShowStep] = useState(true);
  const [checkInId, setCheckInId] = useState<string | null>(existing?.id ?? null);
  const [isSaving, setIsSaving] = useState(false);
  const [completed, setCompleted] = useState(false);
  const [streak, setStreak] = useState(0);

  const [wins, setWins] = useState(existing?.wins ?? "");
  const [friction, setFriction] = useState(existing?.friction ?? "");
  const [energyRating, setEnergyRating] = useState(existing?.energyRating ?? 5);
  const [signalMoment, setSignalMoment] = useState(existing?.signalMoment ?? "");
  const [intentions, setIntentions] = useState(existing?.intentions ?? "");

  useEffect(() => {
    apiFetch<{ streak: number }>("/api/check-ins/streak")
      .then((data) => setStreak(data.streak))
      .catch(() => setStreak(0));
  }, []);

  function getCurrentValue(): string | number {
    switch (step) {
      case 0: return wins;
      case 1: return friction;
      case 2: return energyRating;
      case 3: return signalMoment;
      case 4: return intentions;
      default: return "";
    }
  }

  function buildRequest(): CheckInRequest {
    return {
      wins: wins || undefined,
      friction: friction || undefined,
      energyRating,
      signalMoment: signalMoment || undefined,
      intentions: intentions || undefined,
      completed: step === STEPS.length - 1 ? true : undefined,
    };
  }

  async function saveProgress(): Promise<string | null> {
    setIsSaving(true);
    try {
      const request = buildRequest();
      if (!checkInId) {
        const created = await apiFetch<CheckInResponse>("/api/check-ins", {
          method: "POST",
          body: JSON.stringify(request),
        });
        setCheckInId(created.id);
        return created.id;
      } else {
        await apiFetch<CheckInResponse>(`/api/check-ins/${checkInId}`, {
          method: "PUT",
          body: JSON.stringify(request),
        });
        return checkInId;
      }
    } catch {
      return null;
    } finally {
      setIsSaving(false);
    }
  }

  async function handleNext() {
    const savedId = await saveProgress();
    if (step < STEPS.length - 1) {
      setDirection("forward");
      setShowStep(false);
      setTimeout(() => {
        setStep(step + 1);
        setShowStep(true);
      }, 200);
    } else if (savedId) {
      setCompleted(true);
    }
  }

  function handleBack() {
    if (step > 0) {
      setDirection("backward");
      setShowStep(false);
      setTimeout(() => {
        setStep(step - 1);
        setShowStep(true);
      }, 200);
    }
  }

  if (completed && checkInId) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <CompletionMoment checkInId={checkInId} streak={streak + 1} />
      </div>
    );
  }

  const currentStep = STEPS[step];
  const isLastStep = step === STEPS.length - 1;

  return (
    <div className="mx-auto flex min-h-[60vh] w-full max-w-xl flex-col items-center justify-center px-4 py-8">
      <div className="mb-7 w-full">
        <StepDots total={STEPS.length} current={step} />
      </div>

      <Transition
        show={showStep}
        enter={`transition-all duration-300 ease-out`}
        enterFrom={direction === "forward" ? "translate-x-8 opacity-0" : "-translate-x-8 opacity-0"}
        enterTo="translate-x-0 opacity-100"
        leave="transition-all duration-200 ease-in"
        leaveFrom="translate-x-0 opacity-100"
        leaveTo={direction === "forward" ? "-translate-x-8 opacity-0" : "translate-x-8 opacity-0"}
        className="w-full"
      >
        <div className="rounded-2xl border border-border-default bg-surface p-6 shadow-sm md:p-8">
          <p className="font-mono text-xs uppercase tracking-wider text-text-muted">
            Step {step + 1} of {STEPS.length}
          </p>
          <p className="mt-1 text-xs font-medium text-primary-400">{currentStep.label}</p>

          <h2 className="mt-4 font-serif text-xl font-semibold text-text-primary md:text-[22px]">
            {currentStep.prompt}
          </h2>
          <p className="mt-1 text-sm text-text-secondary">{currentStep.subtitle}</p>

          <div className="mt-6">
            {step === 2 ? (
              <EnergySlider value={energyRating} onChange={setEnergyRating} />
            ) : (
              <textarea
                value={getCurrentValue() as string}
                onChange={(e) => {
                  const val = e.target.value;
                  switch (step) {
                    case 0: setWins(val); break;
                    case 1: setFriction(val); break;
                    case 3: setSignalMoment(val); break;
                    case 4: setIntentions(val); break;
                  }
                }}
                rows={5}
                className="w-full rounded-xl border border-border-input bg-canvas px-4 py-3 text-sm text-text-primary shadow-sm placeholder:text-text-muted focus:border-primary-400 focus:outline-none focus:ring-1 focus:ring-primary-400 resize-none"
                placeholder="Take your time..."
              />
            )}
          </div>

          <div className="mt-6 flex justify-between gap-3">
            {step > 0 ? (
              <button
                onClick={handleBack}
                className="rounded-input border border-border-default px-4 py-2.5 text-sm font-medium text-text-secondary shadow-sm hover:bg-slate-50"
              >
                Back
              </button>
            ) : (
              <div />
            )}
            <button
              onClick={handleNext}
              disabled={isSaving}
              className="rounded-input bg-primary-400 px-6 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
            >
              {isSaving ? "Saving..." : isLastStep ? "Complete" : "Continue"}
            </button>
          </div>
        </div>
      </Transition>
    </div>
  );
}
```

- [ ] **Step 3: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 4: Commit**

```bash
git add web/src/components/CheckInWizard.tsx web/src/components/EnergySlider.tsx
git commit -m "feat: enhanced check-in wizard with card layout, slide transitions, Fraunces typography, completion moment"
```

---

## Task 9: Enhance History Page — Timeline Layout, Expandable Cards, Animations

**Files:**
- Modify: `web/src/components/CheckInCard.tsx`
- Modify: `web/src/app/history/page.tsx`

- [ ] **Step 1: Rewrite CheckInCard.tsx as a timeline card**

Replace the full contents of `web/src/components/CheckInCard.tsx`:

```typescript
"use client";

import { useState } from "react";
import type { CheckInResponse } from "@/lib/types";

function energyDotColor(rating: number | null): string {
  if (!rating) return "bg-border-default";
  if (rating <= 3) return "bg-red-400";
  if (rating <= 6) return "bg-amber-400";
  return "bg-primary-400";
}

function energyBadgeClasses(rating: number | null): string {
  if (!rating) return "bg-slate-100 text-text-muted";
  if (rating <= 3) return "bg-red-50 text-red-700";
  if (rating <= 6) return "bg-amber-50 text-amber-700";
  return "bg-primary-50 text-primary-700";
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", { month: "long", day: "numeric" })}`;
}

function Section({ label, content }: { label: string; content: string | null }) {
  if (!content) return null;
  return (
    <div className="space-y-1">
      <h4 className="text-xs font-medium uppercase tracking-wide text-text-muted">{label}</h4>
      <p className="text-sm text-text-secondary whitespace-pre-wrap">{content}</p>
    </div>
  );
}

interface CheckInCardProps {
  checkIn: CheckInResponse;
  animationDelay?: number;
}

export default function CheckInCard({ checkIn, animationDelay = 0 }: CheckInCardProps) {
  const [expanded, setExpanded] = useState(false);
  const preview = checkIn.wins
    ? checkIn.wins.length > 100 ? checkIn.wins.substring(0, 100) + "..." : checkIn.wins
    : "No wins recorded";

  return (
    <div
      className="relative animate-fade-in"
      style={{ animationDelay: `${animationDelay}ms`, animationFillMode: "backwards" }}
    >
      {/* Timeline dot */}
      <div className={`absolute -left-[24px] top-4 h-2.5 w-2.5 rounded-full ${energyDotColor(checkIn.energyRating)} ring-4 ring-canvas md:-left-[26px] md:h-3 md:w-3`} />

      <button
        onClick={() => setExpanded(!expanded)}
        className="w-full rounded-xl border border-border-default bg-surface p-4 text-left shadow-[0_1px_2px_rgba(0,0,0,0.03)] transition-shadow duration-200 hover:shadow-md md:p-5"
      >
        <div className="flex items-center justify-between mb-2">
          <span className="font-mono text-xs text-text-muted">{formatWeekDate(checkIn.weekStart)}</span>
          <div className="flex items-center gap-2">
            {!checkIn.completed && (
              <span className="rounded-badge bg-amber-50 px-2 py-0.5 text-xs text-amber-700">In progress</span>
            )}
            {checkIn.energyRating && (
              <span className={`rounded-badge px-2.5 py-0.5 text-xs font-medium ${energyBadgeClasses(checkIn.energyRating)}`}>
                {checkIn.energyRating} energy
              </span>
            )}
          </div>
        </div>
        <p className="text-sm text-text-primary line-clamp-2">{preview}</p>
      </button>

      {/* Expandable detail */}
      <div
        className={`overflow-hidden transition-all duration-300 ease-out ${
          expanded ? "max-h-[600px] opacity-100 mt-2" : "max-h-0 opacity-0"
        }`}
      >
        <div className="rounded-xl border border-border-default bg-surface p-4 space-y-4 md:p-5">
          <Section label="Wins" content={checkIn.wins} />
          <Section label="Friction" content={checkIn.friction} />
          <Section label="Signal Moment" content={checkIn.signalMoment} />
          <Section label="Intentions" content={checkIn.intentions} />
          <button
            onClick={() => setExpanded(false)}
            className="text-xs font-medium text-primary-400 hover:text-primary-500"
          >
            Show less
          </button>
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Rewrite history page.tsx with timeline layout and empty state**

Replace the full contents of `web/src/app/history/page.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { ClipboardList } from "lucide-react";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, PaginatedResponse } from "@/lib/types";
import CheckInCard from "@/components/CheckInCard";
import MonthlyInsightCard from "@/components/MonthlyInsightCard";
import Skeleton from "@/components/Skeleton";
import EmptyState from "@/components/EmptyState";

export default function HistoryPage() {
  const [checkIns, setCheckIns] = useState<CheckInResponse[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [hasLoadError, setHasLoadError] = useState(false);

  async function loadPage(pageNum: number) {
    setIsLoading(true);
    setHasLoadError(false);
    try {
      const data = await apiFetch<PaginatedResponse<CheckInResponse>>(
        `/api/check-ins?page=${pageNum}&size=10`
      );
      if (pageNum === 0) {
        setCheckIns(data.content);
      } else {
        setCheckIns((prev) => [...prev, ...data.content]);
      }
      setHasMore(!data.last);
    } catch {
      setHasLoadError(true);
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadPage(0);
  }, []);

  function handleLoadMore() {
    const nextPage = page + 1;
    setPage(nextPage);
    loadPage(nextPage);
  }

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-6 md:text-2xl">
        Your Journey
      </h1>

      <div className="mb-6">
        <MonthlyInsightCard />
      </div>

      {isLoading && checkIns.length === 0 ? (
        <div className="relative pl-7 md:pl-8">
          <div className="absolute left-[8px] top-0 bottom-0 w-px bg-border-default" />
          {[0, 1, 2].map((i) => (
            <div key={i} className="relative mb-5">
              <div className="absolute -left-[24px] top-4 h-2.5 w-2.5 rounded-full bg-border-default ring-4 ring-canvas" />
              <div className="rounded-xl border border-border-default bg-surface p-4">
                <Skeleton className="mb-2 h-3 w-32" />
                <Skeleton className="mb-1 h-4 w-full" />
                <Skeleton className="h-4 w-3/4" />
              </div>
            </div>
          ))}
        </div>
      ) : hasLoadError && checkIns.length === 0 ? (
        <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center">
          <p className="text-sm text-red-700 mb-3">Couldn&apos;t load your check-ins.</p>
          <button onClick={() => loadPage(0)} className="text-sm font-medium text-red-700 hover:text-red-900">
            Try again
          </button>
        </div>
      ) : checkIns.length === 0 ? (
        <EmptyState
          icon={<ClipboardList size={32} />}
          heading="No check-ins yet"
          description="Start your first weekly review to build self-awareness and track patterns over time."
          action={
            <Link
              href="/check-in"
              className="rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500"
            >
              Start your first check-in
            </Link>
          }
        />
      ) : (
        <div className="relative pl-7 md:pl-8">
          {/* Vertical timeline line */}
          <div className="absolute left-[8px] top-0 bottom-0 w-px bg-border-default" />

          {checkIns.map((checkIn, i) => (
            <div key={checkIn.id} className="mb-5">
              <CheckInCard
                checkIn={checkIn}
                animationDelay={i < 10 ? i * 100 : 0}
              />
            </div>
          ))}

          {hasMore && (
            <button
              onClick={handleLoadMore}
              disabled={isLoading}
              className="ml-4 w-[calc(100%-1rem)] rounded-input border border-border-default py-2.5 text-sm text-text-secondary hover:bg-slate-50 disabled:opacity-50"
            >
              {isLoading ? "Loading..." : "Load more"}
            </button>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 3: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 4: Commit**

```bash
git add web/src/components/CheckInCard.tsx web/src/app/history/page.tsx
git commit -m "feat: history page with vertical timeline, expandable cards, staggered animations, empty state"
```

---

## Task 10: Update CheckInDetail to Semantic Colors

**Files:**
- Modify: `web/src/components/CheckInDetail.tsx`

- [ ] **Step 1: Update CheckInDetail.tsx**

Replace the full contents of `web/src/components/CheckInDetail.tsx`:

```typescript
import type { CheckInResponse } from "@/lib/types";
import Link from "next/link";
import InsightSection from "./InsightSection";

function Section({ label, content }: { label: string; content: string | null }) {
  if (!content) return null;
  return (
    <div className="space-y-1">
      <h3 className="text-xs font-medium uppercase tracking-wide text-text-muted">{label}</h3>
      <p className="text-sm text-text-secondary whitespace-pre-wrap">{content}</p>
    </div>
  );
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", { month: "long", day: "numeric", year: "numeric" })}`;
}

interface CheckInDetailProps {
  checkIn: CheckInResponse;
}

export default function CheckInDetail({ checkIn }: CheckInDetailProps) {
  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="font-serif text-xl font-semibold text-text-primary">
          {formatWeekDate(checkIn.weekStart)}
        </h1>
        {checkIn.energyRating && (
          <span className="text-sm text-text-secondary">Energy: {checkIn.energyRating}/10</span>
        )}
      </div>

      <div className="space-y-6 rounded-xl border border-border-default bg-surface p-6 shadow-sm">
        <Section label="Wins" content={checkIn.wins} />
        <Section label="Friction" content={checkIn.friction} />
        <Section label="Signal Moment" content={checkIn.signalMoment} />
        <Section label="Intentions" content={checkIn.intentions} />
      </div>

      {checkIn.completed && (
        <div className="mt-6">
          <InsightSection checkInId={checkIn.id} completed={checkIn.completed} />
        </div>
      )}

      <div className="mt-6 text-center">
        <Link href="/history" className="text-sm font-medium text-primary-400 hover:text-primary-500">
          View all check-ins
        </Link>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Commit**

```bash
git add web/src/components/CheckInDetail.tsx
git commit -m "refactor: update CheckInDetail to semantic colors and typography"
```

---

## Task 11: Update Insight Components — Purple AI Accent

**Files:**
- Modify: `web/src/components/MonthlyInsightCard.tsx`
- Modify: `web/src/components/InsightSection.tsx`

- [ ] **Step 1: Update MonthlyInsightCard.tsx with purple accent**

Replace the full contents of `web/src/components/MonthlyInsightCard.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";
import { Sparkles } from "lucide-react";
import { apiFetch } from "@/lib/api";
import type { MonthlyInsightResponse } from "@/lib/types";

function formatPeriod(start: string, end: string): string {
  const startDate = new Date(start + "T00:00:00");
  const endDate = new Date(end + "T00:00:00");
  const startMonth = startDate.toLocaleDateString("en-GB", { month: "long" });
  const endMonth = endDate.toLocaleDateString("en-GB", { month: "long", year: "numeric" });

  if (startMonth === endMonth.split(" ")[0]) {
    return endMonth;
  }
  return `${startMonth} – ${endMonth}`;
}

export default function MonthlyInsightCard() {
  const [insight, setInsight] = useState<MonthlyInsightResponse | null>(null);

  useEffect(() => {
    apiFetch<MonthlyInsightResponse>("/api/monthly-insights/latest")
      .then(setInsight)
      .catch(() => setInsight(null));
  }, []);

  if (!insight) return null;

  return (
    <div className="rounded-xl border border-purple-200 bg-surface p-6 shadow-[0_1px_2px_rgba(124,58,237,0.06)]">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <div className="flex h-6 w-6 items-center justify-center rounded-md bg-purple-100">
            <Sparkles size={14} className="text-purple-600" />
          </div>
          <h3 className="text-xs font-semibold uppercase tracking-wide text-purple-600">
            Monthly Synthesis
          </h3>
        </div>
        <span className="font-mono text-xs text-text-muted">
          {formatPeriod(insight.periodStart, insight.periodEnd)} · {insight.checkInCount} check-ins
        </span>
      </div>
      <p className="text-sm text-text-primary leading-relaxed whitespace-pre-wrap">
        {insight.content}
      </p>
    </div>
  );
}
```

- [ ] **Step 2: Update InsightSection.tsx with purple accent and semantic colors**

Replace the full contents of `web/src/components/InsightSection.tsx`:

```typescript
"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { Sparkles } from "lucide-react";
import { apiFetch } from "@/lib/api";
import type { InsightResponse } from "@/lib/types";

interface InsightSectionProps {
  checkInId: string;
  completed: boolean;
}

const POLL_INTERVAL_MS = 2_000;
const MAX_POLL_ATTEMPTS = 15;

type Status = "initial-check" | "polling" | "ready" | "missing" | "error";

export default function InsightSection({ checkInId, completed }: InsightSectionProps) {
  const [insight, setInsight] = useState<InsightResponse | null>(null);
  const [status, setStatus] = useState<Status>("initial-check");
  const [generating, setGenerating] = useState(false);
  const attemptsRef = useRef(0);
  const cancelledRef = useRef(false);

  const fetchInsight = useCallback(
    async (mode: "initial-check" | "polling"): Promise<"found" | "missing" | "error"> => {
      try {
        const data = await apiFetch<InsightResponse>(`/api/insights/check-ins/${checkInId}`);
        if (!cancelledRef.current) {
          setInsight(data);
          setStatus("ready");
        }
        return "found";
      } catch (err) {
        const apiError = err as { status?: number };
        if (apiError.status === 404) return "missing";
        if (!cancelledRef.current && mode === "polling") setStatus("error");
        return "error";
      }
    },
    [checkInId]
  );

  useEffect(() => {
    if (!completed) return;
    cancelledRef.current = false;

    (async () => {
      const result = await fetchInsight("initial-check");
      if (!cancelledRef.current && result === "missing") {
        setStatus("missing");
      }
    })();

    return () => {
      cancelledRef.current = true;
    };
  }, [completed, fetchInsight]);

  async function handleGenerate() {
    setGenerating(true);
    try {
      await apiFetch<void>(`/api/insights/check-ins/${checkInId}/generate`, { method: "POST" });
      setStatus("polling");
      attemptsRef.current = 0;
      poll();
    } catch {
      setStatus("error");
    } finally {
      setGenerating(false);
    }
  }

  async function poll() {
    if (cancelledRef.current) return;
    const result = await fetchInsight("polling");
    if (result === "missing") {
      attemptsRef.current += 1;
      if (attemptsRef.current >= MAX_POLL_ATTEMPTS) {
        if (!cancelledRef.current) setStatus("error");
        return;
      }
      setTimeout(poll, POLL_INTERVAL_MS);
    }
  }

  if (!completed) return null;
  if (status === "initial-check") return null;

  if (status === "missing") {
    return (
      <div className="rounded-xl border border-purple-100 bg-purple-50 p-5">
        <div className="flex items-center gap-2 mb-2">
          <Sparkles size={14} className="text-purple-600" />
          <h3 className="text-xs font-medium uppercase tracking-wide text-purple-600">Reflection</h3>
        </div>
        <p className="text-sm text-text-secondary mb-3">Generate a brief AI reflection on this check-in.</p>
        <button
          onClick={handleGenerate}
          disabled={generating}
          className="rounded-input bg-purple-600 px-3 py-1.5 text-sm font-medium text-white shadow-sm hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50"
        >
          {generating ? "Starting..." : "Generate reflection"}
        </button>
      </div>
    );
  }

  if (status === "polling") {
    return (
      <div className="rounded-xl border border-purple-100 bg-purple-50 p-5">
        <div className="flex items-center gap-2 mb-2">
          <svg className="h-4 w-4 animate-spin text-purple-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" aria-hidden="true">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
          </svg>
          <span className="text-xs font-medium uppercase tracking-wide text-purple-600">Reflection</span>
        </div>
        <p className="text-sm italic text-purple-900/70">Reading your check-in...</p>
      </div>
    );
  }

  if (status === "error" || !insight) return null;

  return (
    <div className="rounded-xl border border-purple-100 bg-purple-50 p-5">
      <div className="flex items-center gap-2 mb-2">
        <Sparkles size={14} className="text-purple-600" />
        <h3 className="text-xs font-medium uppercase tracking-wide text-purple-600">Reflection</h3>
      </div>
      <p className="text-sm text-text-primary whitespace-pre-wrap leading-relaxed">{insight.content}</p>
    </div>
  );
}
```

- [ ] **Step 3: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 4: Commit**

```bash
git add web/src/components/MonthlyInsightCard.tsx web/src/components/InsightSection.tsx
git commit -m "feat: update insight components with purple AI accent and semantic colors"
```

---

## Task 12: Enhance Account Page — Streak Hero, Sectioned Cards

**Files:**
- Modify: `web/src/components/StreakBadge.tsx`
- Modify: `web/src/app/account/page.tsx`

- [ ] **Step 1: Rewrite StreakBadge.tsx — remove flame, use dot + NumberTicker**

Replace the full contents of `web/src/components/StreakBadge.tsx`:

```typescript
"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";
import NumberTicker from "./NumberTicker";

function milestoneLabel(streak: number): string | null {
  if (streak >= 52) return "One year";
  if (streak >= 26) return "Half year";
  if (streak >= 12) return "Quarter";
  if (streak >= 4) return "One month";
  return null;
}

function statusBadge(streak: number): { label: string; classes: string } {
  if (streak >= 4) return { label: "On track", classes: "bg-primary-50 text-primary-700" };
  if (streak > 0) return { label: "Just started", classes: "bg-amber-50 text-amber-700" };
  return { label: "Not started", classes: "bg-slate-100 text-text-muted" };
}

function nextMilestone(streak: number): number | null {
  const milestones = [4, 12, 26, 52];
  return milestones.find((m) => m > streak) ?? null;
}

export default function StreakBadge() {
  const [streak, setStreak] = useState<number | null>(null);

  useEffect(() => {
    apiFetch<{ streak: number }>("/api/check-ins/streak")
      .then((data) => setStreak(data.streak))
      .catch(() => setStreak(null));
  }, []);

  if (streak === null) return null;

  const milestone = streak > 0 ? milestoneLabel(streak) : null;
  const status = statusBadge(streak);
  const next = nextMilestone(streak);

  return (
    <div className="rounded-2xl border border-border-default bg-surface p-6 text-center">
      {streak > 0 ? (
        <>
          <div className="flex items-center justify-center gap-2 mb-1">
            <span className="inline-block h-3 w-3 rounded-full bg-primary-400" />
          </div>
          <div className="font-serif text-4xl font-bold text-text-primary">
            <NumberTicker value={streak} />
          </div>
          <p className="text-sm text-text-secondary">
            week{streak !== 1 ? "s" : ""} streak
          </p>
          <div className="mt-3 flex items-center justify-center gap-2">
            {milestone && (
              <span className="rounded-badge bg-amber-50 px-2.5 py-0.5 text-xs font-medium text-amber-700">
                {milestone}
              </span>
            )}
            <span className={`rounded-badge px-2.5 py-0.5 text-xs font-medium ${status.classes}`}>
              {status.label}
            </span>
            {next && (
              <span className="rounded-badge bg-slate-100 px-2.5 py-0.5 text-xs text-text-muted">
                Next: {next}w
              </span>
            )}
          </div>
        </>
      ) : (
        <>
          <div className="flex items-center justify-center gap-2 mb-1">
            <span className="inline-block h-3 w-3 rounded-full bg-border-default" />
          </div>
          <p className="font-medium text-text-secondary">No streak yet</p>
          <p className="text-sm text-text-muted">Your streak starts with your next check-in</p>
        </>
      )}
    </div>
  );
}
```

- [ ] **Step 2: Update account/page.tsx — sectioned cards, semantic colors**

This is a large file. The key changes are:
- Replace all `text-gray-*` with `text-text-primary`, `text-text-secondary`, `text-text-muted`
- Replace all `border-gray-200` with `border-border-default`
- Replace all `bg-primary-600` with `bg-primary-400`, `text-white` with `text-primary-900`
- Replace all `focus:ring-primary-500` with `focus:ring-primary-400`
- Replace all `border-gray-200` on inputs with `border-border-input`
- Replace `rounded-lg` with `rounded-xl` on section containers, `rounded-input` on inputs/buttons
- Wrap each section in a card: `rounded-xl border border-border-default bg-surface p-6`
- Change `<h1>` to use `font-serif`
- Add `<StreakBadge />` at the top before the Profile section

In `web/src/app/account/page.tsx`, add the StreakBadge import:

```typescript
import StreakBadge from "@/components/StreakBadge";
```

Then in the `AccountPage` component JSX, add it as the first child of the content area, right after `<h1>`:

```typescript
<h1 className="font-serif text-2xl font-semibold text-text-primary">Account</h1>
<StreakBadge />
```

Then update all section wrappers. Each `<section>` should be wrapped in a card container. For example, `ProfileSection`'s outer `<section>` becomes:

```typescript
<section className="rounded-xl border border-border-default bg-surface p-6">
```

Replace `<hr className="border-gray-200" />` dividers between sections with just spacing (`space-y-4` on the parent or `gap-4` if using flex/grid), since the cards provide their own visual separation.

Apply the color token substitutions throughout all sub-components (`ProfileSection`, `ChangePasswordSection`, `NotificationsSection`, `SubscriptionSection`).

- [ ] **Step 3: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 4: Commit**

```bash
git add web/src/components/StreakBadge.tsx web/src/app/account/page.tsx
git commit -m "feat: account page with streak hero, sectioned cards, semantic colors"
```

---

## Task 13: Create Auth Layout and Floating Input Components

**Files:**
- Create: `web/src/components/AuthLayout.tsx`
- Create: `web/src/components/FloatingInput.tsx`

- [ ] **Step 1: Create AuthLayout.tsx**

Create `web/src/components/AuthLayout.tsx`:

```typescript
import type { ReactNode } from "react";

interface AuthLayoutProps {
  children: ReactNode;
}

export default function AuthLayout({ children }: AuthLayoutProps) {
  return (
    <div className="grid min-h-[calc(100vh-56px)] lg:grid-cols-2">
      {/* Brand panel — desktop only */}
      <div className="hidden lg:flex flex-col justify-between bg-gradient-to-br from-primary-900 to-slate-900 p-10 text-white">
        <span className="font-serif text-2xl font-semibold">Reflect</span>
        <div>
          <p className="text-lg italic leading-relaxed opacity-80">
            &ldquo;The weekly pause that made me actually notice my patterns.&rdquo;
          </p>
          <p className="mt-2 text-sm opacity-50">— Early beta user</p>
        </div>
      </div>

      {/* Form panel */}
      <div className="flex items-center justify-center bg-canvas px-4 py-12">
        <div className="w-full max-w-sm">
          {children}
        </div>
      </div>
    </div>
  );
}
```

- [ ] **Step 2: Create FloatingInput.tsx**

Create `web/src/components/FloatingInput.tsx`:

```typescript
interface FloatingInputProps {
  id: string;
  label: string;
  type?: string;
  autoComplete?: string;
  value: string;
  onChange: (value: string) => void;
  error?: string;
  hint?: string;
}

export default function FloatingInput({
  id,
  label,
  type = "text",
  autoComplete,
  value,
  onChange,
  error,
  hint,
}: FloatingInputProps) {
  const hasError = !!error;

  return (
    <div>
      <div className="relative">
        <input
          id={id}
          type={type}
          autoComplete={autoComplete}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder=" "
          aria-invalid={hasError}
          aria-describedby={error ? `${id}-error` : hint ? `${id}-hint` : undefined}
          className={`peer w-full rounded-input border bg-surface px-4 pb-2 pt-6 text-sm text-text-primary shadow-sm transition-colors focus:outline-none focus:ring-1 ${
            hasError
              ? "border-red-400 focus:border-red-500 focus:ring-red-500"
              : "border-border-input focus:border-primary-400 focus:ring-primary-400"
          }`}
        />
        <label
          htmlFor={id}
          className="pointer-events-none absolute left-4 top-4 origin-top-left text-sm text-text-muted transition-all duration-200 peer-placeholder-shown:translate-y-0 peer-placeholder-shown:scale-100 peer-focus:-translate-y-3 peer-focus:scale-[0.85] peer-focus:text-primary-400 peer-[:not(:placeholder-shown)]:-translate-y-3 peer-[:not(:placeholder-shown)]:scale-[0.85]"
        >
          {label}
        </label>
      </div>
      {error && (
        <p id={`${id}-error`} className="mt-1 text-xs text-red-600">{error}</p>
      )}
      {!error && hint && (
        <p id={`${id}-hint`} className="mt-1 text-xs text-text-muted">{hint}</p>
      )}
    </div>
  );
}
```

- [ ] **Step 3: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 4: Commit**

```bash
git add web/src/components/AuthLayout.tsx web/src/components/FloatingInput.tsx
git commit -m "feat: add AuthLayout split layout and FloatingInput with floating labels"
```

---

## Task 14: Enhance Auth Pages — Login, Register, Forgot/Reset Password

**Files:**
- Modify: `web/src/app/login/page.tsx`
- Modify: `web/src/app/register/page.tsx`
- Modify: `web/src/app/forgot-password/page.tsx`
- Modify: `web/src/app/reset-password/page.tsx`

- [ ] **Step 1: Rewrite login/page.tsx**

Replace the full contents of `web/src/app/login/page.tsx`:

```typescript
"use client";

import { FormEvent, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import AuthLayout from "@/components/AuthLayout";
import FloatingInput from "@/components/FloatingInput";

interface FieldErrors {
  email?: string;
  password?: string;
  form?: string;
}

export default function LoginPage() {
  const { login } = useAuth();
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validate(): FieldErrors {
    const next: FieldErrors = {};
    if (!email) next.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) next.email = "Enter a valid email";
    if (!password) next.password = "Password is required";
    return next;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors({});
    setIsSubmitting(true);

    try {
      await login(email, password);
      router.push("/check-in");
    } catch (err) {
      const apiError = err as ApiError;
      setErrors({ form: apiError.error || "Sign in failed. Please try again." });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout>
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-1">Welcome back</h1>
      <p className="text-sm text-text-secondary mb-8">Sign in to continue your practice</p>

      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        {errors.form && (
          <div role="alert" className="rounded-input bg-red-50 border border-red-200 p-3 text-sm text-red-600">
            {errors.form}
          </div>
        )}

        <FloatingInput
          id="email"
          label="Email"
          type="email"
          autoComplete="email"
          value={email}
          onChange={(v) => { setEmail(v); if (errors.email) setErrors({ ...errors, email: undefined }); }}
          error={errors.email}
        />

        <FloatingInput
          id="password"
          label="Password"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={(v) => { setPassword(v); if (errors.password) setErrors({ ...errors, password: undefined }); }}
          error={errors.password}
        />

        <div className="flex justify-end">
          <Link href="/forgot-password" className="text-xs text-primary-400 hover:text-primary-500">
            Forgot password?
          </Link>
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSubmitting ? "Signing in..." : "Sign in"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-text-secondary">
        Don&apos;t have an account?{" "}
        <Link href="/register" className="font-medium text-primary-400 hover:text-primary-500">Sign up</Link>
      </p>
    </AuthLayout>
  );
}
```

- [ ] **Step 2: Rewrite register/page.tsx**

Replace the full contents of `web/src/app/register/page.tsx`:

```typescript
"use client";

import { FormEvent, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import AuthLayout from "@/components/AuthLayout";
import FloatingInput from "@/components/FloatingInput";

interface FieldErrors {
  displayName?: string;
  email?: string;
  password?: string;
  form?: string;
}

export default function RegisterPage() {
  const { register } = useAuth();
  const router = useRouter();
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validate(): FieldErrors {
    const next: FieldErrors = {};
    if (!displayName.trim()) next.displayName = "Name is required";
    if (!email) next.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) next.email = "Enter a valid email";
    if (!password) next.password = "Password is required";
    else if (password.length < 8) next.password = "At least 8 characters";
    return next;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors({});
    setIsSubmitting(true);

    try {
      await register(email, password, displayName);
      router.push("/check-in");
    } catch (err) {
      const apiError = err as ApiError;
      const message = apiError.error || "Sign up failed. Please try again.";
      if (/email/i.test(message) && /(exist|registered|taken)/i.test(message)) {
        setErrors({ email: "This email is already registered" });
      } else {
        setErrors({ form: message });
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout>
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-1">Start reflecting</h1>
      <p className="text-sm text-text-secondary mb-8">Create your account to begin</p>

      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        {errors.form && (
          <div role="alert" className="rounded-input bg-red-50 border border-red-200 p-3 text-sm text-red-600">
            {errors.form}
          </div>
        )}

        <FloatingInput
          id="displayName"
          label="Name"
          autoComplete="name"
          value={displayName}
          onChange={(v) => { setDisplayName(v); if (errors.displayName) setErrors({ ...errors, displayName: undefined }); }}
          error={errors.displayName}
        />

        <FloatingInput
          id="email"
          label="Email"
          type="email"
          autoComplete="email"
          value={email}
          onChange={(v) => { setEmail(v); if (errors.email) setErrors({ ...errors, email: undefined }); }}
          error={errors.email}
        />

        <FloatingInput
          id="password"
          label="Password"
          type="password"
          autoComplete="new-password"
          value={password}
          onChange={(v) => { setPassword(v); if (errors.password) setErrors({ ...errors, password: undefined }); }}
          error={errors.password}
          hint="At least 8 characters"
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSubmitting ? "Creating account..." : "Create account"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-text-secondary">
        Already have an account?{" "}
        <Link href="/login" className="font-medium text-primary-400 hover:text-primary-500">Sign in</Link>
      </p>
    </AuthLayout>
  );
}
```

- [ ] **Step 3: Update forgot-password and reset-password pages**

For `web/src/app/forgot-password/page.tsx` and `web/src/app/reset-password/page.tsx`, apply the same pattern:
- Wrap in `<AuthLayout>`
- Replace inputs with `<FloatingInput>`
- Update heading copy: "Reset your password" / "We'll send you a link"
- Replace all color tokens (gray → semantic, primary-600 → primary-400)
- Replace `rounded-lg` with `rounded-input`

Read each file first, then apply the substitutions following the same pattern as login and register.

- [ ] **Step 4: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 5: Commit**

```bash
git add web/src/app/login/page.tsx web/src/app/register/page.tsx web/src/app/forgot-password/page.tsx web/src/app/reset-password/page.tsx
git commit -m "feat: enhanced auth pages with split layout, floating labels, and calm copy"
```

---

## Task 15: Update Remaining Components — PricingCards, UpgradePrompt, VerificationBanner

**Files:**
- Modify: `web/src/components/PricingCards.tsx`
- Modify: `web/src/components/UpgradePrompt.tsx`
- Modify: `web/src/components/VerificationBanner.tsx`

- [ ] **Step 1: Update PricingCards.tsx**

Apply these substitutions throughout `web/src/components/PricingCards.tsx`:
- `border-gray-200` → `border-border-default`
- `text-gray-900` → `text-text-primary`
- `text-gray-500` → `text-text-secondary`
- `rounded-lg` → `rounded-xl` (cards), `rounded-input` (buttons)
- `bg-primary-600` → `bg-primary-400`, `text-white` → `text-primary-900`
- `hover:bg-primary-700` → `hover:bg-primary-500`
- `border-2 border-primary-600` → `border-2 border-primary-400`
- `bg-primary-600` (Save badge) → `bg-primary-400`
- `focus:ring-primary-500` → `focus:ring-primary-400`

- [ ] **Step 2: Update UpgradePrompt.tsx**

Apply the same substitutions. Also update the icon color from `text-primary-600` to `text-primary-400` and `bg-primary-50` stays the same.

- [ ] **Step 3: Update VerificationBanner.tsx**

Read the file first, then apply semantic color substitutions.

- [ ] **Step 4: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 5: Commit**

```bash
git add web/src/components/PricingCards.tsx web/src/components/UpgradePrompt.tsx web/src/components/VerificationBanner.tsx
git commit -m "refactor: update PricingCards, UpgradePrompt, VerificationBanner to semantic colors"
```

---

## Task 16: Add Page Transitions

**Files:**
- Modify: `web/src/app/layout.tsx`

- [ ] **Step 1: Add a page transition wrapper**

In `web/src/app/layout.tsx`, wrap the `<main>` children in a transition container. Since Next.js App Router doesn't have a built-in route transition hook, use a simple CSS fade-in on the main content:

Replace:

```typescript
<main className="pb-16 md:pb-0">{children}</main>
```

With:

```typescript
<main className="animate-fade-in pb-16 md:pb-0">{children}</main>
```

This applies a 300ms opacity fade-in when the page first renders. Route transitions in App Router re-render the `children` prop, which triggers the animation.

- [ ] **Step 2: Verify build**

```bash
cd web && npm run build
```

Expected: build succeeds.

- [ ] **Step 3: Commit**

```bash
git add web/src/app/layout.tsx
git commit -m "feat: add subtle page transition fade-in"
```

---

## Task 17: Final Verification — Build, Lint, Visual Check

- [ ] **Step 1: Run full build**

```bash
cd web && npm run build
```

Expected: build succeeds with no errors.

- [ ] **Step 2: Run lint**

```bash
cd web && npm run lint
```

Expected: no lint errors (warnings are OK).

- [ ] **Step 3: Start dev server and visually verify**

```bash
cd web && npm run dev
```

Open in browser and check:
- Canvas background is warm off-white (#FAFAF9)
- DM Sans renders for body text, Fraunces for headings
- All buttons are emerald green (#34D399), not indigo
- Check-in wizard has card layout with step dots and slide transitions
- History page has vertical timeline with color-coded dots
- Insight cards have purple accent borders
- Auth pages have split layout on desktop, form-only on mobile
- Header has frosted glass effect on scroll
- Bottom nav visible on mobile widths
- Streak badge shows `● {number}` (no fire emoji)
- Empty states render with dashed border + icon + CTA

- [ ] **Step 4: Commit any final adjustments**

```bash
git add -A
git commit -m "fix: final UI/UX polish adjustments"
```

---

## Deferred (Requires Backend Work)

These items from the spec are **not included** in this plan because they require API changes:

- **Energy Trend Card** (Phase 3) — needs an aggregated energy endpoint (`GET /api/check-ins/energy-trend`)
- **Pattern Signal Cards** (Phase 3) — needs pattern signal API from PatternSentinelAgent
- **Insights empty state with progress ring** (Phase 3) — needs a check-in count endpoint or the count included in existing responses

These should be implemented when the Phase 2 (AI Insights) API work is completed.

---

## Summary

| Task | Description | Files Changed |
|------|-------------|---------------|
| 1 | Install dependencies | package.json |
| 2 | Tailwind config (palette, tokens, fonts, animations) | tailwind.config.ts |
| 3 | Font loading + globals | layout.tsx, globals.css |
| 4 | Utility components (NumberTicker, Skeleton, EmptyState, useScrolled) | 4 new files |
| 5 | Spinner semantic colors | Spinner.tsx |
| 6 | Header (frosted glass, streak badge) + BottomNav | Header.tsx, BottomNav.tsx, layout.tsx |
| 7 | StepDots + CompletionMoment | 2 new files |
| 8 | Check-in wizard enhancement | CheckInWizard.tsx, EnergySlider.tsx |
| 9 | History timeline | CheckInCard.tsx, history/page.tsx |
| 10 | CheckInDetail semantic colors | CheckInDetail.tsx |
| 11 | Insight components purple accent | MonthlyInsightCard.tsx, InsightSection.tsx |
| 12 | Account page (streak hero, sections) | StreakBadge.tsx, account/page.tsx |
| 13 | AuthLayout + FloatingInput | 2 new files |
| 14 | Auth pages (login, register, forgot, reset) | 4 page files |
| 15 | PricingCards, UpgradePrompt, VerificationBanner | 3 files |
| 16 | Page transitions | layout.tsx |
| 17 | Final verification | build + lint + visual |
