"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse } from "@/lib/types";
import CheckInWizard from "@/components/CheckInWizard";

export default function NewCheckInPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const [existing, setExisting] = useState<CheckInResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !user) { router.replace("/login"); return; }
    if (!authLoading && user) {
      apiFetch<CheckInResponse>("/api/check-ins/current")
        .then((data) => { setExisting(data); setIsLoading(false); })
        .catch(() => { setIsLoading(false); });
    }
  }, [authLoading, user, router]);

  if (authLoading || isLoading) {
    return <div className="mx-auto w-full max-w-xl px-4 py-8"><p className="text-sm text-gray-400">Loading...</p></div>;
  }
  return <CheckInWizard existing={existing} />;
}
