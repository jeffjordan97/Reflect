"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { ClipboardList } from "lucide-react";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, PaginatedResponse } from "@/lib/types";
import CheckInCard from "@/components/CheckInCard";
import MonthlyInsightCard from "@/components/MonthlyInsightCard";
import Skeleton from "@/components/Skeleton";
import EmptyState from "@/components/EmptyState";

export default function HistoryPage() {
  const [checkIns, setCheckIns] = useState<CheckInResponse[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [hasLoadError, setHasLoadError] = useState(false);

  async function loadPage(pageNum: number) {
    setIsLoading(true);
    setHasLoadError(false);
    try {
      const data = await apiFetch<PaginatedResponse<CheckInResponse>>(
        `/api/check-ins?page=${pageNum}&size=10`
      );
      if (pageNum === 0) {
        setCheckIns(data.content);
      } else {
        setCheckIns((prev) => [...prev, ...data.content]);
      }
      setHasMore(!data.last);
    } catch {
      setHasLoadError(true);
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadPage(0);
  }, []);

  function handleLoadMore() {
    const nextPage = page + 1;
    setPage(nextPage);
    loadPage(nextPage);
  }

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-6">
        Your Journey
      </h1>

      <div className="mb-6">
        <MonthlyInsightCard />
      </div>

      {isLoading && checkIns.length === 0 ? (
        <div className="relative pl-7 md:pl-8">
          <div className="absolute left-[8px] top-0 bottom-0 w-px bg-border-default" />
          {[0, 1, 2].map((i) => (
            <div key={i} className="relative mb-5">
              <div className="absolute -left-[24px] top-4 h-2.5 w-2.5 rounded-full bg-border-default ring-4 ring-canvas" />
              <div className="rounded-xl border border-border-default bg-surface p-4">
                <Skeleton className="mb-2 h-3 w-32" />
                <Skeleton className="mb-1 h-4 w-full" />
                <Skeleton className="h-4 w-3/4" />
              </div>
            </div>
          ))}
        </div>
      ) : hasLoadError && checkIns.length === 0 ? (
        <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center">
          <p className="text-sm text-red-700 mb-3">Couldn&apos;t load your check-ins.</p>
          <button onClick={() => loadPage(0)} className="text-sm font-medium text-red-700 hover:text-red-900">
            Try again
          </button>
        </div>
      ) : checkIns.length === 0 ? (
        <EmptyState
          icon={<ClipboardList size={32} />}
          heading="No check-ins yet"
          description="Start your first weekly review to build self-awareness and track patterns over time."
          action={
            <Link
              href="/check-in"
              className="rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500"
            >
              Start your first check-in
            </Link>
          }
        />
      ) : (
        <div className="relative pl-7 md:pl-8">
          <div className="absolute left-[8px] top-0 bottom-0 w-px bg-border-default" />
          {checkIns.map((checkIn, i) => (
            <div key={checkIn.id} className="mb-5">
              <CheckInCard
                checkIn={checkIn}
                animationDelay={i < 10 ? i * 100 : 0}
              />
            </div>
          ))}
          {hasMore && (
            <button
              onClick={handleLoadMore}
              disabled={isLoading}
              className="ml-4 w-[calc(100%-1rem)] rounded-input border border-border-default py-2.5 text-sm text-text-secondary hover:bg-slate-50 disabled:opacity-50"
            >
              {isLoading ? "Loading..." : "Load more"}
            </button>
          )}
        </div>
      )}
    </div>
  );
}
