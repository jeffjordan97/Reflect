"use client";

import { useState } from "react";
import { useAuth } from "@/lib/auth";
import { apiFetch } from "@/lib/api";

export default function VerificationBanner() {
  const { user } = useAuth();
  const [sent, setSent] = useState(false);
  const [sending, setSending] = useState(false);

  if (!user || user.emailVerified) return null;

  async function handleResend() {
    setSending(true);
    try {
      await apiFetch("/api/auth/resend-verification", { method: "POST" });
      setSent(true);
    } catch {
      // Silently fail
    } finally {
      setSending(false);
    }
  }

  return (
    <div className="border-b border-amber-200 bg-amber-50 px-4 py-2.5">
      <div className="mx-auto flex max-w-3xl items-center justify-between">
        <p className="text-sm text-amber-800">
          Please verify your email address to complete your account setup.
        </p>
        {sent ? (
          <span className="text-sm text-amber-600">Email sent</span>
        ) : (
          <button
            onClick={handleResend}
            disabled={sending}
            className="text-sm font-medium text-amber-700 hover:text-amber-900 disabled:opacity-50"
          >
            {sending ? "Sending..." : "Resend email"}
          </button>
        )}
      </div>
    </div>
  );
}
