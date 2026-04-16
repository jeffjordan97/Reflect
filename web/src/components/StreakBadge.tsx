"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";

function FlameIcon({ streak }: { streak: number }) {
  // Flame color intensifies with streak length
  const baseColor = streak >= 12 ? "#DC2626" : streak >= 4 ? "#F59E0B" : "#FB923C";
  const tipColor = streak >= 12 ? "#F59E0B" : streak >= 4 ? "#FBBF24" : "#FDE68A";

  return (
    <svg
      width="32"
      height="32"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={streak > 0 ? "animate-pulse" : ""}
      style={{ animationDuration: "3s" }}
      aria-hidden="true"
    >
      <defs>
        <linearGradient id="flame-grad" x1="16" y1="28" x2="16" y2="4" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor={baseColor} />
          <stop offset="60%" stopColor={tipColor} />
          <stop offset="100%" stopColor="#FEF3C7" />
        </linearGradient>
        <linearGradient id="flame-inner" x1="16" y1="26" x2="16" y2="14" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor={baseColor} />
          <stop offset="100%" stopColor={tipColor} />
        </linearGradient>
      </defs>
      {/* Outer flame */}
      <path
        d="M16 3C16 3 8 12 8 19C8 23.4183 11.5817 27 16 27C20.4183 27 24 23.4183 24 19C24 12 16 3 16 3Z"
        fill="url(#flame-grad)"
      />
      {/* Inner flame */}
      <path
        d="M16 12C16 12 12.5 17 12.5 20.5C12.5 22.433 14.067 24 16 24C17.933 24 19.5 22.433 19.5 20.5C19.5 17 16 12 16 12Z"
        fill="url(#flame-inner)"
        opacity="0.9"
      />
      {/* Bright core */}
      <ellipse cx="16" cy="21.5" rx="2" ry="2.5" fill="#FEF9C3" opacity="0.7" />
    </svg>
  );
}

function GrayFlameIcon() {
  return (
    <svg
      width="28"
      height="28"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <path
        d="M16 3C16 3 8 12 8 19C8 23.4183 11.5817 27 16 27C20.4183 27 24 23.4183 24 19C24 12 16 3 16 3Z"
        fill="#E5E7EB"
      />
      <path
        d="M16 12C16 12 12.5 17 12.5 20.5C12.5 22.433 14.067 24 16 24C17.933 24 19.5 22.433 19.5 20.5C19.5 17 16 12 16 12Z"
        fill="#D1D5DB"
      />
    </svg>
  );
}

function milestoneLabel(streak: number): string | null {
  if (streak >= 52) return "One year";
  if (streak >= 26) return "Half year";
  if (streak >= 12) return "Quarter";
  if (streak >= 4) return "One month";
  return null;
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

  return (
    <div className="rounded-lg border border-gray-200 bg-white px-5 py-4 shadow-sm">
      {streak > 0 ? (
        <div className="flex items-center gap-3">
          <FlameIcon streak={streak} />
          <div className="flex flex-col">
            <span className="text-3xl font-bold tracking-tight text-gray-900">
              {streak}
            </span>
            <div className="flex items-center gap-2">
              <span className="text-xs text-gray-400">
                {streak} week{streak !== 1 ? "s" : ""} streak
              </span>
              {milestone && (
                <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-700">
                  {milestone}
                </span>
              )}
            </div>
          </div>
        </div>
      ) : (
        <div className="flex items-center gap-3">
          <GrayFlameIcon />
          <div>
            <p className="text-sm font-medium text-gray-700">No streak yet</p>
            <p className="text-xs text-gray-400">Complete a check-in to start</p>
          </div>
        </div>
      )}
    </div>
  );
}
