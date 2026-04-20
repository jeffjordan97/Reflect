"use client";

import Link from "next/link";
import PricingCards from "./PricingCards";

export default function UpgradePrompt() {
  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="rounded-lg border border-gray-200 bg-white p-8 shadow-sm text-center">
        <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary-50">
          <svg
            className="h-6 w-6 text-primary-600"
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

        <PricingCards />

        <div className="mt-6">
          <Link
            href="/history"
            className="text-sm text-gray-500 hover:text-gray-700"
          >
            View your history
          </Link>
        </div>
      </div>
    </div>
  );
}
