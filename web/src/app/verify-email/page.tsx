"use client";

import { useEffect, useState, useCallback, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { apiFetch } from "@/lib/api";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import Spinner from "@/components/Spinner";

function VerifyEmailContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token");

  const [status, setStatus] = useState<"loading" | "success" | "error">(
    token ? "loading" : "error"
  );
  const [errorMessage, setErrorMessage] = useState(
    token ? "" : "Invalid verification link."
  );
  const [resending, setResending] = useState(false);
  const [resent, setResent] = useState(false);

  const verify = useCallback(async () => {
    if (!token) return;
    try {
      await apiFetch("/api/auth/verify-email", {
        method: "POST",
        body: JSON.stringify({ token }),
      });
      setStatus("success");
    } catch (err) {
      const apiError = err as ApiError;
      setErrorMessage(
        apiError.error || "This link is invalid or has expired."
      );
      setStatus("error");
    }
  }, [token]);

  useEffect(() => {
    verify();
  }, [verify]);

  async function handleResend() {
    setResending(true);
    try {
      await apiFetch("/api/auth/resend-verification", { method: "POST" });
      setResent(true);
    } catch {
      // Silently fail
    } finally {
      setResending(false);
    }
  }

  if (status === "loading") {
    return <Spinner label="Verifying your email..." />;
  }

  if (status === "success") {
    return (
      <div className="space-y-6">
        <div className="rounded-lg bg-green-50 border border-green-200 p-4 text-sm text-green-700">
          Your email has been verified.
        </div>
        <p className="text-center">
          <Link
            href="/check-in"
            className="inline-block rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2"
          >
            Continue to Reflect
          </Link>
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="rounded-lg bg-red-50 border border-red-200 p-4 text-sm text-red-600">
        {errorMessage}
      </div>
      <div className="text-center">
        {resent ? (
          <span className="text-sm text-gray-500">
            Verification email sent.
          </span>
        ) : (
          <button
            onClick={handleResend}
            disabled={resending}
            className="text-sm font-medium text-primary-600 hover:text-primary-700 disabled:opacity-50"
          >
            {resending ? "Sending..." : "Resend verification email"}
          </button>
        )}
      </div>
    </div>
  );
}

export default function VerifyEmailPage() {
  return (
    <div className="flex items-start justify-center px-4 pt-16 pb-16">
      <div className="w-full max-w-sm text-center">
        <h1 className="text-2xl font-semibold tracking-tight mb-8">
          Email verification
        </h1>
        <Suspense fallback={<Spinner label="Loading..." />}>
          <VerifyEmailContent />
        </Suspense>
      </div>
    </div>
  );
}
