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
