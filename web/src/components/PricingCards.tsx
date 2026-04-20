"use client";

import { useState } from "react";
import { apiFetch } from "@/lib/api";

const PRICE_MONTHLY = "price_1TMn512zANdBD1v5CIyWvVzW";
const PRICE_ANNUAL = "price_1TMn512zANdBD1v5q1do1WQb";

interface CheckoutResponse {
  url: string;
}

export default function PricingCards() {
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
    <div>
      {error && (
        <div
          role="alert"
          className="mb-4 rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600"
        >
          {error}
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        <div className="rounded-lg border border-gray-200 p-5">
          <p className="text-sm font-medium text-gray-900 mb-1">Monthly</p>
          <p className="text-2xl font-bold text-gray-900 mb-1">
            &pound;7.99
            <span className="text-sm font-normal text-gray-500">/mo</span>
          </p>
          <button
            onClick={() => handleCheckout(PRICE_MONTHLY)}
            disabled={loadingPlan !== null}
            className="mt-4 w-full rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {loadingPlan === PRICE_MONTHLY ? "Redirecting..." : "Go monthly"}
          </button>
        </div>

        <div className="rounded-lg border-2 border-primary-600 p-5 relative">
          <span className="absolute -top-2.5 left-1/2 -translate-x-1/2 rounded-full bg-primary-600 px-2.5 py-0.5 text-xs font-medium text-white">
            Save 37%
          </span>
          <p className="text-sm font-medium text-gray-900 mb-1">Annual</p>
          <p className="text-2xl font-bold text-gray-900 mb-1">
            &pound;59.99
            <span className="text-sm font-normal text-gray-500">/yr</span>
          </p>
          <button
            onClick={() => handleCheckout(PRICE_ANNUAL)}
            disabled={loadingPlan !== null}
            className="mt-4 w-full rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {loadingPlan === PRICE_ANNUAL ? "Redirecting..." : "Go annual"}
          </button>
        </div>
      </div>
    </div>
  );
}
