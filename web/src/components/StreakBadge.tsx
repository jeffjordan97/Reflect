"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";

export default function StreakBadge() {
  const [streak, setStreak] = useState<number | null>(null);

  useEffect(() => {
    apiFetch<{ streak: number }>("/api/check-ins/streak")
      .then((data) => setStreak(data.streak))
      .catch(() => setStreak(null));
  }, []);

  if (streak === null) return null;

  return (
    <div className="flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-4 py-3 shadow-sm">
      {streak > 0 ? (
        <>
          <span className="text-2xl" role="img" aria-label="streak">
            🔥
          </span>
          <div>
            <span className="text-2xl font-semibold text-gray-900">{streak}</span>
            <span className="ml-1 text-sm text-gray-500">
              week{streak === 1 ? "" : ""} streak
            </span>
          </div>
        </>
      ) : (
        <p className="text-sm text-gray-500">
          Start your streak this Sunday
        </p>
      )}
    </div>
  );
}
