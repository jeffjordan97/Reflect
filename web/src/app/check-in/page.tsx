"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInWizard from "@/components/CheckInWizard";
import CheckInDetail from "@/components/CheckInDetail";
import Spinner from "@/components/Spinner";
import StreakBadge from "@/components/StreakBadge";

export default function CheckInPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const [checkIn, setCheckIn] = useState<CheckInResponse | null>(null);
  const [notFound, setNotFound] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !user) { router.replace("/login"); return; }
    if (!authLoading && user) {
      apiFetch<CheckInResponse>("/api/check-ins/current")
        .then((data) => { setCheckIn(data); setIsLoading(false); })
        .catch(() => { setNotFound(true); setIsLoading(false); });
    }
  }, [authLoading, user, router]);

  if (authLoading || isLoading) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <Spinner />
      </div>
    );
  }
  if (notFound) return <CheckInWizard />;
  if (checkIn && !checkIn.completed) return <CheckInWizard existing={checkIn} />;
  if (checkIn && checkIn.completed) return (
    <>
      <div className="mx-auto w-full max-w-xl px-4 pt-8">
        <StreakBadge />
      </div>
      <CheckInDetail checkIn={checkIn} />
    </>
  );
  return <CheckInWizard />;
}
