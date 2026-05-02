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

type Status = "initial-check" | "polling" | "ready" | "error";

export default function InsightSection({ checkInId, completed }: InsightSectionProps) {
  const [insight, setInsight] = useState<InsightResponse | null>(null);
  const [status, setStatus] = useState<Status>("initial-check");
  const attemptsRef = useRef(0);
  const cancelledRef = useRef(false);

  const fetchInsight = useCallback(
    async (): Promise<"found" | "missing" | "error"> => {
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
        return "error";
      }
    },
    [checkInId]
  );

  const triggerAndPoll = useCallback(async () => {
    setStatus("polling");
    attemptsRef.current = 0;

    try {
      await apiFetch<void>(`/api/insights/check-ins/${checkInId}/generate`, { method: "POST" });
    } catch {
      // Backend may have already triggered generation on completion — poll anyway
    }

    function poll() {
      if (cancelledRef.current) return;
      fetchInsight().then((result) => {
        if (cancelledRef.current) return;
        if (result === "missing") {
          attemptsRef.current += 1;
          if (attemptsRef.current >= MAX_POLL_ATTEMPTS) {
            setStatus("error");
            return;
          }
          setTimeout(poll, POLL_INTERVAL_MS);
        } else if (result === "error") {
          setStatus("error");
        }
      });
    }

    poll();
  }, [checkInId, fetchInsight]);

  useEffect(() => {
    if (!completed) return;
    cancelledRef.current = false;

    (async () => {
      const result = await fetchInsight();
      if (cancelledRef.current) return;
      if (result === "missing") {
        triggerAndPoll();
      }
    })();

    return () => {
      cancelledRef.current = true;
    };
  }, [completed, fetchInsight, triggerAndPoll]);

  if (!completed) return null;
  if (status === "initial-check") return null;

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

  if (status === "error") {
    return (
      <div className="rounded-xl border border-purple-100 bg-purple-50 p-5">
        <div className="flex items-center gap-2 mb-2">
          <Sparkles size={14} className="text-purple-600" />
          <h3 className="text-xs font-medium uppercase tracking-wide text-purple-600">Reflection</h3>
        </div>
        <p className="text-sm text-text-secondary mb-3">Reflection couldn&apos;t be generated right now.</p>
        <button
          onClick={triggerAndPoll}
          className="rounded-input bg-purple-600 px-3 py-1.5 text-sm font-medium text-white shadow-sm hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
        >
          Retry
        </button>
      </div>
    );
  }

  if (!insight) return null;

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
