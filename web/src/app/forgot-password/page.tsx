"use client";

import { FormEvent, useState } from "react";
import { apiFetch } from "@/lib/api";
import Link from "next/link";
import AuthLayout from "@/components/AuthLayout";
import FloatingInput from "@/components/FloatingInput";

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
    <AuthLayout>
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-1">Reset your password</h1>
      <p className="text-sm text-text-secondary mb-8">We&apos;ll send you a reset link</p>

      {submitted ? (
        <div className="space-y-6">
          <div className="rounded-input bg-green-50 border border-green-200 p-4 text-sm text-green-700">
            If that email is registered, we&apos;ve sent a reset link.
          </div>
          <p className="text-center text-sm text-text-secondary">
            <Link
              href="/login"
              className="font-medium text-primary-400 hover:text-primary-500"
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
                className="rounded-input bg-red-50 border border-red-200 p-3 text-sm text-red-600"
              >
                {errors.form}
              </div>
            )}

            <FloatingInput
              id="email"
              label="Email"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(v) => {
                setEmail(v);
                if (errors.email) setErrors({ ...errors, email: undefined });
              }}
              error={errors.email}
            />

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
            >
              {isSubmitting ? "Sending..." : "Send reset link"}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-text-secondary">
            <Link
              href="/login"
              className="font-medium text-primary-400 hover:text-primary-500"
            >
              Back to sign in
            </Link>
          </p>
        </>
      )}
    </AuthLayout>
  );
}
