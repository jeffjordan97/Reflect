"use client";

import { useEffect, useRef, useState } from "react";
import { apiFetch } from "@/lib/api";
import type { InsightResponse } from "@/lib/types";

interface InsightSectionProps {
  checkInId: string;
  /**
   * Whether the check-in is completed. The insight is only generated after
   * completion, so we only poll for it when the check-in is complete.
   */
  completed: boolean;
}

// Poll every 2 seconds for up to 30 seconds total
const POLL_INTERVAL_MS = 2_000;
const MAX_POLL_ATTEMPTS = 15;

type Status = "loading" | "ready" | "timeout" | "error";

export default function InsightSection({ checkInId, completed }: InsightSectionProps) {
  const [insight, setInsight] = useState<InsightResponse | null>(null);
  const [status, setStatus] = useState<Status>("loading");
  const attemptsRef = useRef(0);

  useEffect(() => {
    if (!completed) {
      setStatus("ready");
      return;
    }

    let cancelled = false;
    let timer: ReturnType<typeof setTimeout> | null = null;
    attemptsRef.current = 0;

    async function poll() {
      if (cancelled) return;

      try {
        const data = await apiFetch<InsightResponse>(
          `/api/insights/check-ins/${checkInId}`
        );
        if (!cancelled) {
          setInsight(data);
          setStatus("ready");
        }
      } catch (err) {
        const apiError = err as { status?: number };
        // 404 = not generated yet, keep polling
        if (apiError.status === 404) {
          attemptsRef.current += 1;
          if (attemptsRef.current >= MAX_POLL_ATTEMPTS) {
            if (!cancelled) setStatus("timeout");
            return;
          }
          timer = setTimeout(poll, POLL_INTERVAL_MS);
        } else {
          // Any other error — give up quietly
          if (!cancelled) setStatus("error");
        }
      }
    }

    poll();

    return () => {
      cancelled = true;
      if (timer) clearTimeout(timer);
    };
  }, [checkInId, completed]);

  if (!completed) return null;

  if (status === "loading") {
    return (
      <div className="rounded-lg border border-primary-100 bg-primary-50 p-5">
        <div className="flex items-center gap-2 mb-2">
          <svg
            className="h-4 w-4 animate-spin text-primary-600"
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
          <span className="text-xs font-medium uppercase tracking-wide text-primary-700">
            Reflection
          </span>
        </div>
        <p className="text-sm text-primary-900/70 italic">
          Reading your check-in...
        </p>
      </div>
    );
  }

  if (status === "timeout" || status === "error" || !insight) {
    // Quiet graceful degradation — no alarming error message
    return null;
  }

  return (
    <div className="rounded-lg border border-primary-100 bg-primary-50 p-5">
      <h3 className="text-xs font-medium uppercase tracking-wide text-primary-700 mb-2">
        Reflection
      </h3>
      <p className="text-sm text-gray-800 whitespace-pre-wrap leading-relaxed">
        {insight.content}
      </p>
    </div>
  );
}
