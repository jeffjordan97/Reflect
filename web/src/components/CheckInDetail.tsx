import type { CheckInResponse } from "@/lib/types";
import Link from "next/link";
import InsightSection from "./InsightSection";

function Section({ label, content }: { label: string; content: string | null }) {
  if (!content) return null;
  return (
    <div className="space-y-1">
      <h3 className="text-xs font-medium uppercase tracking-wide text-text-muted">{label}</h3>
      <p className="text-sm text-text-secondary whitespace-pre-wrap">{content}</p>
    </div>
  );
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", { month: "long", day: "numeric", year: "numeric" })}`;
}

interface CheckInDetailProps {
  checkIn: CheckInResponse;
}

export default function CheckInDetail({ checkIn }: CheckInDetailProps) {
  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="font-serif text-xl font-semibold text-text-primary">
          {formatWeekDate(checkIn.weekStart)}
        </h1>
        {checkIn.energyRating && (
          <span className="text-sm text-text-secondary">Energy: {checkIn.energyRating}/10</span>
        )}
      </div>

      <div className="space-y-6 rounded-xl border border-border-default bg-surface p-6 shadow-sm">
        <Section label="Wins" content={checkIn.wins} />
        <Section label="Friction" content={checkIn.friction} />
        <Section label="Signal Moment" content={checkIn.signalMoment} />
        <Section label="Intentions" content={checkIn.intentions} />
      </div>

      {checkIn.completed && (
        <div className="mt-6">
          <InsightSection checkInId={checkIn.id} completed={checkIn.completed} />
        </div>
      )}

      <div className="mt-6 text-center">
        <Link href="/history" className="text-sm font-medium text-primary-400 hover:text-primary-500">
          View all check-ins
        </Link>
      </div>
    </div>
  );
}
