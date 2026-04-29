"use client";

import { FormEvent, useState } from "react";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { ApiError } from "@/lib/types";
import AuthLayout from "@/components/AuthLayout";
import FloatingInput from "@/components/FloatingInput";

interface FieldErrors {
  displayName?: string;
  email?: string;
  password?: string;
  form?: string;
}

export default function RegisterPage() {
  const { register } = useAuth();
  const router = useRouter();
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validate(): FieldErrors {
    const next: FieldErrors = {};
    if (!displayName.trim()) next.displayName = "Name is required";
    if (!email) next.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) next.email = "Enter a valid email";
    if (!password) next.password = "Password is required";
    else if (password.length < 8) next.password = "At least 8 characters";
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
      await register(email, password, displayName);
      router.push("/check-in");
    } catch (err) {
      const apiError = err as ApiError;
      const message = apiError.error || "Sign up failed. Please try again.";
      if (/email/i.test(message) && /(exist|registered|taken)/i.test(message)) {
        setErrors({ email: "This email is already registered" });
      } else {
        setErrors({ form: message });
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout>
      <h1 className="font-serif text-2xl font-semibold text-text-primary mb-1">Start reflecting</h1>
      <p className="text-sm text-text-secondary mb-8">Create your account to begin</p>

      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        {errors.form && (
          <div role="alert" className="rounded-input bg-red-50 border border-red-200 p-3 text-sm text-red-600">
            {errors.form}
          </div>
        )}

        <FloatingInput
          id="displayName"
          label="Name"
          autoComplete="name"
          value={displayName}
          onChange={(v) => { setDisplayName(v); if (errors.displayName) setErrors({ ...errors, displayName: undefined }); }}
          error={errors.displayName}
        />

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
          autoComplete="new-password"
          value={password}
          onChange={(v) => { setPassword(v); if (errors.password) setErrors({ ...errors, password: undefined }); }}
          error={errors.password}
          hint="At least 8 characters"
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-input bg-primary-400 px-4 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
        >
          {isSubmitting ? "Creating account..." : "Create account"}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-text-secondary">
        Already have an account?{" "}
        <Link href="/login" className="font-medium text-primary-400 hover:text-primary-500">Sign in</Link>
      </p>
    </AuthLayout>
  );
}
