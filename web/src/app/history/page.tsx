"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, PaginatedResponse } from "@/lib/types";
import CheckInCard from "@/components/CheckInCard";
import Spinner from "@/components/Spinner";

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
      <h1 className="text-xl font-semibold text-gray-900 mb-6">Your check-ins</h1>

      {isLoading && checkIns.length === 0 ? (
        <Spinner />
      ) : hasLoadError && checkIns.length === 0 ? (
        <div className="rounded-lg border border-red-200 bg-red-50 p-6 text-center">
          <p className="text-sm text-red-700 mb-3">Couldn&apos;t load your check-ins.</p>
          <button
            onClick={() => loadPage(0)}
            className="text-sm font-medium text-red-700 hover:text-red-900"
          >
            Try again
          </button>
        </div>
      ) : checkIns.length === 0 ? (
        <div className="rounded-lg border border-gray-200 bg-gray-50 p-8 text-center">
          <p className="text-sm text-gray-700 mb-1 font-medium">No check-ins yet</p>
          <p className="text-sm text-gray-500 mb-4">
            Take a few minutes to reflect on your week.
          </p>
          <Link
            href="/check-in"
            className="inline-block rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700"
          >
            Start your first check-in
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {checkIns.map((checkIn) => (
            <CheckInCard key={checkIn.id} checkIn={checkIn} />
          ))}
          {hasMore && (
            <button
              onClick={handleLoadMore}
              disabled={isLoading}
              className="w-full rounded-lg border border-gray-200 py-2 text-sm text-gray-500 hover:bg-gray-50 disabled:opacity-50"
            >
              {isLoading ? "Loading..." : "Load more"}
            </button>
          )}
        </div>
      )}
    </div>
  );
}
