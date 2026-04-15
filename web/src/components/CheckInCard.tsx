import Link from "next/link";
import type { CheckInResponse } from "@/lib/types";

function energyBadgeColor(rating: number | null): string {
  if (!rating) return "bg-gray-100 text-gray-500";
  if (rating <= 3) return "bg-red-100 text-red-700";
  if (rating <= 5) return "bg-amber-100 text-amber-700";
  if (rating <= 7) return "bg-emerald-100 text-emerald-700";
  return "bg-emerald-200 text-emerald-800";
}

function formatWeekDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  return `Week of ${date.toLocaleDateString("en-GB", { month: "long", day: "numeric", year: "numeric" })}`;
}

export default function CheckInCard({ checkIn }: { checkIn: CheckInResponse }) {
  const preview = checkIn.wins
    ? checkIn.wins.length > 100 ? checkIn.wins.substring(0, 100) + "..." : checkIn.wins
    : "No wins recorded";

  return (
    <Link href={`/history/${checkIn.id}`}>
      <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-900">{formatWeekDate(checkIn.weekStart)}</span>
          <div className="flex items-center gap-2">
            {!checkIn.completed && (
              <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs text-amber-700">In progress</span>
            )}
            {checkIn.energyRating && (
              <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${energyBadgeColor(checkIn.energyRating)}`}>
                Energy: {checkIn.energyRating}
              </span>
            )}
          </div>
        </div>
        <p className="text-sm text-gray-500 line-clamp-2">{preview}</p>
      </div>
    </Link>
  );
}
