"use client";

import { useEffect, useState } from "react";
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
    <div className="rounded-lg border border-primary-200 bg-gradient-to-br from-primary-50 to-white p-6 shadow-sm">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-xs font-semibold uppercase tracking-wide text-primary-700">
          Monthly Reflection
        </h3>
        <span className="text-xs text-gray-400">
          {formatPeriod(insight.periodStart, insight.periodEnd)} · {insight.checkInCount} check-ins
        </span>
      </div>
      <p className="text-sm text-gray-800 leading-relaxed whitespace-pre-wrap">
        {insight.content}
      </p>
    </div>
  );
}
