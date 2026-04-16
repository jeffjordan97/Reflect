"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { apiFetch } from "@/lib/api";
import type { InsightResponse } from "@/lib/types";

interface InsightSectionProps {
  checkInId: string;
  /**
   * Whether the check-in is completed. The insight is only generated after
   * completion, so we only look for it when the check-in is complete.
   */
  completed: boolean;
}

// Poll every 2 seconds for up to 30 seconds total
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
        const data = await apiFetch<InsightResponse>(
          `/api/insights/check-ins/${checkInId}`
        );
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

  // On mount: check if an insight already exists. If not, show a generate prompt.
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
      await apiFetch<void>(`/api/insights/check-ins/${checkInId}/generate`, {
        method: "POST",
      });
      // Start polling
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

  if (status === "initial-check") return null; // nothing visible while we check

  if (status === "missing") {
    return (
      <div className="rounded-lg border border-primary-100 bg-primary-50 p-5">
        <h3 className="text-xs font-medium uppercase tracking-wide text-primary-700 mb-2">
          Reflection
        </h3>
        <p className="text-sm text-gray-600 mb-3">
          Generate a brief AI reflection on this check-in.
        </p>
        <button
          onClick={handleGenerate}
          disabled={generating}
          className="rounded-lg bg-primary-600 px-3 py-1.5 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
        >
          {generating ? "Starting..." : "Generate reflection"}
        </button>
      </div>
    );
  }

  if (status === "polling") {
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
        <p className="text-sm text-primary-900/70 italic">Reading your check-in...</p>
      </div>
    );
  }

  if (status === "error" || !insight) {
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
