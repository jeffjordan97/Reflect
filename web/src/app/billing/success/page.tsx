"use client";

import Link from "next/link";

export default function BillingSuccessPage() {
  return (
    <div className="flex items-start justify-center px-4 pt-20 pb-16">
      <div className="w-full max-w-md text-center">
        {/* Green checkmark */}
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-green-50">
          <svg
            className="h-8 w-8 text-green-600"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={2}
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M4.5 12.75l6 6 9-13.5"
            />
          </svg>
        </div>

        <h1 className="text-2xl font-semibold tracking-tight text-gray-900 mb-2">
          Welcome to Pro
        </h1>
        <p className="text-sm text-gray-500 mb-8">
          Your subscription is active. Enjoy unlimited check-ins and AI
          reflections.
        </p>

        <Link
          href="/check-in"
          className="inline-block rounded-lg bg-indigo-600 px-6 py-2.5 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
        >
          Start your check-in
        </Link>
      </div>
    </div>
  );
}
