"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInDetail from "@/components/CheckInDetail";

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
    return <div className="mx-auto w-full max-w-xl px-4 py-8"><p className="text-sm text-gray-500">Loading...</p></div>;
  }
  if (!checkIn) {
    return <div className="mx-auto w-full max-w-xl px-4 py-8"><p className="text-sm text-gray-500">Check-in not found.</p></div>;
  }
  return <CheckInDetail checkIn={checkIn} />;
}
