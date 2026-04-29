"use client";

import { FormEvent, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import AuthLayout from "@/components/AuthLayout";
import FloatingInput from "@/components/FloatingInput";

interface FieldErrors {
  email?: string;
  password?: string;
  form?: string;
}

export default function LoginPage() {
  const { login } = useAuth();
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validate(): FieldErrors {
    const next: FieldErrors = {};
    if (!email) next.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) next.email = "Enter a valid email";
    if (!password) next.password = "Password is required";
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
      await login(email, password);
      router.push("/check-in");
    } catch (err) {
      const apiError = err as ApiError;
      setErrors({ form: apiError.error || "Sign in failed. Please try again." });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout>
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-1">Welcome back</h1>
      <p className="text-sm text-text-secondary mb-8">Sign in to continue your practice</p>

      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        {errors.form && (
          <div role="alert" className="rounded-input bg-red-50 border border-red-200 p-3 text-sm text-red-600">
            {errors.form}
          </div>
        )}

        <FloatingInput
          id="email"
          label="Email"
          type="email"
          autoComplete="email"
          value={email}
          onChange={(v) => { setEmail(v); if (errors.email) setErrors({ ...errors, email: undefined }); }}
          error={errors.email}
        />

        <FloatingInput
          id="password"
          label="Password"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={(v) => { setPassword(v); if (errors.password) setErrors({ ...errors, password: undefined }); }}
          error={errors.password}
        />

        <div className="flex justify-end">
          <Link href="/forgot-password" className="text-xs text-primary-400 hover:text-primary-500">
            Forgot password?
          </Link>
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSubmitting ? "Signing in..." : "Sign in"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-text-secondary">
        Don&apos;t have an account?{" "}
        <Link href="/register" className="font-medium text-primary-400 hover:text-primary-500">Sign up</Link>
      </p>
    </AuthLayout>
  );
}
