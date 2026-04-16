"use client";

import { FormEvent, useState } from "react";
import { apiFetch } from "@/lib/api";
import Link from "next/link";

interface FieldErrors {
  email?: string;
  form?: string;
}

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  function validate(): FieldErrors {
    const next: FieldErrors = {};
    if (!email) next.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
      next.email = "Enter a valid email";
    return next;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors({});
    setIsSubmitting(true);

    try {
      await apiFetch("/api/auth/forgot-password", {
        method: "POST",
        body: JSON.stringify({ email }),
      });
    } catch {
      // Always show success to prevent email enumeration
    } finally {
      setIsSubmitting(false);
      setSubmitted(true);
    }
  }

  return (
    <div className="flex items-start justify-center px-4 pt-16 pb-16">
      <div className="w-full max-w-sm">
        <h1 className="text-2xl font-semibold tracking-tight text-center mb-2">
          Reset your password
        </h1>
        <p className="text-sm text-gray-500 text-center mb-8">
          Enter your email and we&apos;ll send you a reset link.
        </p>

        {submitted ? (
          <div className="space-y-6">
            <div className="rounded-lg bg-green-50 border border-green-200 p-4 text-sm text-green-700">
              If that email is registered, we&apos;ve sent a reset link.
            </div>
            <p className="text-center text-sm text-gray-500">
              <Link
                href="/login"
                className="text-primary-600 hover:text-primary-700 font-medium"
              >
                Back to sign in
              </Link>
            </p>
          </div>
        ) : (
          <>
            <form onSubmit={handleSubmit} noValidate className="space-y-4">
              {errors.form && (
                <div
                  role="alert"
                  className="rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600"
                >
                  {errors.form}
                </div>
              )}

              <div>
                <label
                  htmlFor="email"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Email
                </label>
                <input
                  id="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (errors.email)
                      setErrors({ ...errors, email: undefined });
                  }}
                  aria-invalid={!!errors.email}
                  aria-describedby={errors.email ? "email-error" : undefined}
                  className={`w-full rounded-lg border px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-1 ${
                    errors.email
                      ? "border-red-400 focus:border-red-500 focus:ring-red-500"
                      : "border-gray-200 focus:border-primary-500 focus:ring-primary-500"
                  }`}
                  placeholder="you@example.com"
                />
                {errors.email && (
                  <p id="email-error" className="mt-1 text-xs text-red-600">
                    {errors.email}
                  </p>
                )}
              </div>

              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
              >
                {isSubmitting ? "Sending..." : "Send reset link"}
              </button>
            </form>

            <p className="mt-6 text-center text-sm text-gray-500">
              <Link
                href="/login"
                className="text-primary-600 hover:text-primary-700 font-medium"
              >
                Back to sign in
              </Link>
            </p>
          </>
        )}
      </div>
    </div>
  );
}
