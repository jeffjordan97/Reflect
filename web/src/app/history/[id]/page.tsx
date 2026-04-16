"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInDetail from "@/components/CheckInDetail";
import Spinner from "@/components/Spinner";

export default function CheckInDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const [checkIn, setCheckIn] = useState<CheckInResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const data = await apiFetch<CheckInResponse>(`/api/check-ins/${id}`);
        setCheckIn(data);
      } catch {
        // Not found
      }
      setIsLoading(false);
    }
    load();
  }, [id]);

  if (isLoading) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <Spinner />
      </div>
    );
  }

  if (!checkIn) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <div className="rounded-lg border border-gray-200 bg-gray-50 p-8 text-center">
          <p className="text-sm text-gray-700 mb-4">Check-in not found.</p>
          <Link
            href="/history"
            className="text-sm font-medium text-primary-600 hover:text-primary-700"
          >
            Back to history
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="mx-auto w-full max-w-xl px-4 pt-6">
        <Link
          href="/history"
          className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-900"
        >
          <span aria-hidden="true">&larr;</span>
          Back to history
        </Link>
      </div>
      <CheckInDetail checkIn={checkIn} />
    </div>
  );
}
