"use client";

import { FormEvent, useState } from "react";
import { useSearchParams } from "next/navigation";
import { Suspense } from "react";
import { apiFetch } from "@/lib/api";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import AuthLayout from "@/components/AuthLayout";
import FloatingInput from "@/components/FloatingInput";
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
        <div className="rounded-input bg-red-50 border border-red-200 p-4 text-sm text-red-600">
          Invalid reset link. Please request a new password reset.
        </div>
        <Link
          href="/forgot-password"
          className="text-sm font-medium text-primary-400 hover:text-primary-500"
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
        <div className="rounded-input bg-green-50 border border-green-200 p-4 text-sm text-green-700">
          Your password has been updated.
        </div>
        <p className="text-center text-sm text-text-secondary">
          <Link
            href="/login"
            className="font-medium text-primary-400 hover:text-primary-500"
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
            className="rounded-input bg-red-50 border border-red-200 p-3 text-sm text-red-600"
          >
            {errors.form}
          </div>
        )}

        <FloatingInput
          id="newPassword"
          label="New password"
          type="password"
          autoComplete="new-password"
          value={newPassword}
          onChange={(v) => {
            setNewPassword(v);
            if (errors.newPassword) setErrors({ ...errors, newPassword: undefined });
          }}
          error={errors.newPassword}
          hint="At least 8 characters"
        />

        <FloatingInput
          id="confirmPassword"
          label="Confirm password"
          type="password"
          autoComplete="new-password"
          value={confirmPassword}
          onChange={(v) => {
            setConfirmPassword(v);
            if (errors.confirmPassword) setErrors({ ...errors, confirmPassword: undefined });
          }}
          error={errors.confirmPassword}
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSubmitting ? "Resetting..." : "Reset password"}
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
  );
}

export default function ResetPasswordPage() {
  return (
    <AuthLayout>
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-1">Set a new password</h1>
      <p className="text-sm text-text-secondary mb-8">Enter your new password below</p>
      <Suspense fallback={<Spinner label="Loading..." />}>
        <ResetPasswordForm />
      </Suspense>
    </AuthLayout>
  );
}
