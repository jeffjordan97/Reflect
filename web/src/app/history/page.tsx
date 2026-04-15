"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, PaginatedResponse } from "@/lib/types";
import CheckInCard from "@/components/CheckInCard";

export default function HistoryPage() {
  const [checkIns, setCheckIns] = useState<CheckInResponse[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(true);

  async function loadPage(pageNum: number) {
    setIsLoading(true);
    try {
      const data = await apiFetch<PaginatedResponse<CheckInResponse>>(`/api/check-ins?page=${pageNum}&size=10`);
      if (pageNum === 0) {
        setCheckIns(data.content);
      } else {
        setCheckIns((prev) => [...prev, ...data.content]);
      }
      setHasMore(!data.last);
    } catch {
      // Failed to load
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => { loadPage(0); }, []);

  function handleLoadMore() {
    const nextPage = page + 1;
    setPage(nextPage);
    loadPage(nextPage);
  }

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <h1 className="text-xl font-semibold text-gray-900 mb-6">Your check-ins</h1>
      {isLoading && checkIns.length === 0 ? (
        <p className="text-sm text-gray-500">Loading...</p>
      ) : checkIns.length === 0 ? (
        <p className="text-sm text-gray-500">No check-ins yet. Start your first one!</p>
      ) : (
        <div className="space-y-3">
          {checkIns.map((checkIn) => <CheckInCard key={checkIn.id} checkIn={checkIn} />)}
          {hasMore && (
            <button onClick={handleLoadMore} disabled={isLoading}
              className="w-full rounded-lg border border-gray-200 py-2 text-sm text-gray-500 hover:bg-gray-50 disabled:opacity-50">
              {isLoading ? "Loading..." : "Load more"}
            </button>
          )}
        </div>
      )}
    </div>
  );
}
