"use client";

import { FormEvent, useState } from "react";
import { useSearchParams } from "next/navigation";
import { Suspense } from "react";
import { apiFetch } from "@/lib/api";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import Spinner from "@/components/Spinner";

interface FieldErrors {
  newPassword?: string;
  confirmPassword?: string;
  form?: string;
}

function ResetPasswordForm() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token");

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);

  if (!token) {
    return (
      <div className="space-y-4 text-center">
        <div className="rounded-lg bg-red-50 border border-red-200 p-4 text-sm text-red-600">
          Invalid reset link. Please request a new password reset.
        </div>
        <Link
          href="/forgot-password"
          className="text-sm text-primary-600 hover:text-primary-700 font-medium"
        >
          Request new reset link
        </Link>
      </div>
    );
  }

  function validate(): FieldErrors {
    const next: FieldErrors = {};
    if (!newPassword) next.newPassword = "Password is required";
    else if (newPassword.length < 8)
      next.newPassword = "At least 8 characters";
    if (!confirmPassword) next.confirmPassword = "Please confirm your password";
    else if (newPassword !== confirmPassword)
      next.confirmPassword = "Passwords do not match";
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
      await apiFetch("/api/auth/reset-password", {
        method: "POST",
        body: JSON.stringify({ token, newPassword }),
      });
      setSuccess(true);
    } catch (err) {
      const apiError = err as ApiError;
      setErrors({
        form:
          apiError.error ||
          "Failed to reset password. The link may be expired.",
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  if (success) {
    return (
      <div className="space-y-6">
        <div className="rounded-lg bg-green-50 border border-green-200 p-4 text-sm text-green-700">
          Your password has been updated.
        </div>
        <p className="text-center text-sm text-gray-500">
          <Link
            href="/login"
            className="text-primary-600 hover:text-primary-700 font-medium"
          >
            Sign in with your new password
          </Link>
        </p>
      </div>
    );
  }

  return (
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
            htmlFor="newPassword"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            New password
          </label>
          <input
            id="newPassword"
            type="password"
            autoComplete="new-password"
            value={newPassword}
            onChange={(e) => {
              setNewPassword(e.target.value);
              if (errors.newPassword)
                setErrors({ ...errors, newPassword: undefined });
            }}
            aria-invalid={!!errors.newPassword}
            aria-describedby={
              errors.newPassword ? "newPassword-error" : "newPassword-hint"
            }
            className={`w-full rounded-lg border px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-1 ${
              errors.newPassword
                ? "border-red-400 focus:border-red-500 focus:ring-red-500"
                : "border-gray-200 focus:border-primary-500 focus:ring-primary-500"
            }`}
            placeholder="At least 8 characters"
          />
          {errors.newPassword ? (
            <p id="newPassword-error" className="mt-1 text-xs text-red-600">
              {errors.newPassword}
            </p>
          ) : (
            <p id="newPassword-hint" className="mt-1 text-xs text-gray-400">
              At least 8 characters
            </p>
          )}
        </div>

        <div>
          <label
            htmlFor="confirmPassword"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Confirm password
          </label>
          <input
            id="confirmPassword"
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            onChange={(e) => {
              setConfirmPassword(e.target.value);
              if (errors.confirmPassword)
                setErrors({ ...errors, confirmPassword: undefined });
            }}
            aria-invalid={!!errors.confirmPassword}
            aria-describedby={
              errors.confirmPassword ? "confirmPassword-error" : undefined
            }
            className={`w-full rounded-lg border px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-1 ${
              errors.confirmPassword
                ? "border-red-400 focus:border-red-500 focus:ring-red-500"
                : "border-gray-200 focus:border-primary-500 focus:ring-primary-500"
            }`}
            placeholder="Repeat your password"
          />
          {errors.confirmPassword && (
            <p
              id="confirmPassword-error"
              className="mt-1 text-xs text-red-600"
            >
              {errors.confirmPassword}
            </p>
          )}
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSubmitting ? "Resetting..." : "Reset password"}
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
  );
}

export default function ResetPasswordPage() {
  return (
    <div className="flex items-start justify-center px-4 pt-16 pb-16">
      <div className="w-full max-w-sm">
        <h1 className="text-2xl font-semibold tracking-tight text-center mb-8">
          Set a new password
        </h1>
        <Suspense fallback={<Spinner label="Loading..." />}>
          <ResetPasswordForm />
        </Suspense>
      </div>
    </div>
  );
}
