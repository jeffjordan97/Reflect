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
