import type { CheckInResponse } from "@/lib/types";
import Link from "next/link";

function Section({ label, content }: { label: string; content: string | null }) {
  if (!content) return null;
  return (
    <div className="space-y-1">
      <h3 className="text-xs font-medium uppercase tracking-wide text-gray-400">{label}</h3>
      <p className="text-sm text-gray-700 whitespace-pre-wrap">{content}</p>
    </div>
  );
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", { month: "long", day: "numeric", year: "numeric" })}`;
}

export default function CheckInDetail({ checkIn }: { checkIn: CheckInResponse }) {
  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-xl font-semibold text-gray-900">{formatWeekDate(checkIn.weekStart)}</h1>
        {checkIn.energyRating && <span className="text-sm text-gray-500">Energy: {checkIn.energyRating}/10</span>}
      </div>
      <div className="space-y-6 rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <Section label="Wins" content={checkIn.wins} />
        <Section label="Friction" content={checkIn.friction} />
        <Section label="Signal Moment" content={checkIn.signalMoment} />
        <Section label="Intentions" content={checkIn.intentions} />
      </div>
      <div className="mt-6 text-center">
        <Link href="/history" className="text-sm text-primary-600 hover:text-primary-700 font-medium">View all check-ins</Link>
      </div>
    </div>
  );
}
