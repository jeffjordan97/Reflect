"use client";

import { useState } from "react";
import Link from "next/link";
import { apiFetch } from "@/lib/api";

const PRICE_MONTHLY = "price_1TMn512zANdBD1v5CIyWvVzW";
const PRICE_ANNUAL = "price_1TMn512zANdBD1v5q1do1WQb";

interface CheckoutResponse {
  url: string;
}

export default function UpgradePrompt() {
  const [loadingPlan, setLoadingPlan] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function handleCheckout(priceId: string) {
    setLoadingPlan(priceId);
    setError(null);
    try {
      const response = await apiFetch<CheckoutResponse>(
        "/api/billing/checkout",
        {
          method: "POST",
          body: JSON.stringify({ priceId }),
        }
      );
      window.location.href = response.url;
    } catch {
      setError("Something went wrong. Please try again.");
      setLoadingPlan(null);
    }
  }

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="rounded-lg border border-gray-200 bg-white p-8 shadow-sm text-center">
        <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-indigo-50">
          <svg
            className="h-6 w-6 text-indigo-600"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={1.5}
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09ZM18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.455 2.456L21.75 6l-1.036.259a3.375 3.375 0 0 0-2.455 2.456Z"
            />
          </svg>
        </div>

        <h2 className="text-xl font-semibold text-gray-900 mb-2">
          You&apos;ve used your 4 free check-ins
        </h2>
        <p className="text-sm text-gray-500 mb-8">
          Upgrade to Pro for unlimited weekly reflections and AI insights.
        </p>

        {error && (
          <div
            role="alert"
            className="mb-6 rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600"
          >
            {error}
          </div>
        )}

        <div className="grid grid-cols-2 gap-4 mb-6">
          {/* Monthly card */}
          <div className="rounded-lg border border-gray-200 p-5">
            <p className="text-sm font-medium text-gray-900 mb-1">Monthly</p>
            <p className="text-2xl font-bold text-gray-900 mb-1">
              &pound;2.99
              <span className="text-sm font-normal text-gray-500">/mo</span>
            </p>
            <button
              onClick={() => handleCheckout(PRICE_MONTHLY)}
              disabled={loadingPlan !== null}
              className="mt-4 w-full rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
            >
              {loadingPlan === PRICE_MONTHLY ? "Redirecting..." : "Go monthly"}
            </button>
          </div>

          {/* Annual card (highlighted) */}
          <div className="rounded-lg border-2 border-indigo-600 p-5 relative">
            <span className="absolute -top-2.5 left-1/2 -translate-x-1/2 rounded-full bg-indigo-600 px-2.5 py-0.5 text-xs font-medium text-white">
              Save 17%
            </span>
            <p className="text-sm font-medium text-gray-900 mb-1">Annual</p>
            <p className="text-2xl font-bold text-gray-900 mb-1">
              &pound;29.99
              <span className="text-sm font-normal text-gray-500">/yr</span>
            </p>
            <button
              onClick={() => handleCheckout(PRICE_ANNUAL)}
              disabled={loadingPlan !== null}
              className="mt-4 w-full rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
            >
              {loadingPlan === PRICE_ANNUAL ? "Redirecting..." : "Go annual"}
            </button>
          </div>
        </div>

        <Link
          href="/history"
          className="text-sm text-gray-500 hover:text-gray-700"
        >
          View your history
        </Link>
      </div>
    </div>
  );
}
