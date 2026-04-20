"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, PaginatedResponse } from "@/lib/types";
import CheckInWizard from "@/components/CheckInWizard";
import CheckInDetail from "@/components/CheckInDetail";
import UpgradePrompt from "@/components/UpgradePrompt";
import Spinner from "@/components/Spinner";
import StreakBadge from "@/components/StreakBadge";

const FREE_TIER_LIMIT = 4;

export default function CheckInPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const [checkIn, setCheckIn] = useState<CheckInResponse | null>(null);
  const [notFound, setNotFound] = useState(false);
  const [billingBlocked, setBillingBlocked] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !user) { router.replace("/login"); return; }
    if (!authLoading && user) {
      apiFetch<CheckInResponse>("/api/check-ins/current")
        .then((data) => { setCheckIn(data); setIsLoading(false); })
        .catch(async () => {
          setNotFound(true);
          // If the user is not Pro, check whether they've hit the free tier limit
          if (!user.pro) {
            try {
              const history = await apiFetch<PaginatedResponse<CheckInResponse>>(
                "/api/check-ins?page=0&size=1"
              );
              if (history.totalElements >= FREE_TIER_LIMIT) {
                setBillingBlocked(true);
              }
            } catch {
              // If we can't check, let them try — the backend will enforce the limit
            }
          }
          setIsLoading(false);
        });
    }
  }, [authLoading, user, router]);

  if (authLoading || isLoading) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <Spinner />
      </div>
    );
  }
  if (notFound && billingBlocked) return <UpgradePrompt />;
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
