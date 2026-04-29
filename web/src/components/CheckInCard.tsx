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
