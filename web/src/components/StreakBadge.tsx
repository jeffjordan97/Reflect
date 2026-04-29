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
          <div className="mt-3 flex items-center justify-center gap-2 flex-wrap">
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
