"use client";

import { FormEvent, useState, useCallback } from "react";
import { useAuth } from "@/lib/auth";
import { apiFetch } from "@/lib/api";
import { useRouter } from "next/navigation";
import Spinner from "@/components/Spinner";
import PricingCards from "@/components/PricingCards";
import type { ApiError, UserResponse } from "@/lib/types";

interface ProfileErrors {
  displayName?: string;
  form?: string;
}

interface PasswordErrors {
  currentPassword?: string;
  newPassword?: string;
  confirmPassword?: string;
  form?: string;
}

function ProfileSection({
  user,
  onUpdate,
}: {
  user: UserResponse;
  onUpdate: (updated: UserResponse) => void;
}) {
  const [displayName, setDisplayName] = useState(user.displayName);
  const [errors, setErrors] = useState<ProfileErrors>({});
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  async function handleSave(e: FormEvent) {
    e.preventDefault();
    const next: ProfileErrors = {};
    if (!displayName.trim()) next.displayName = "Name is required";
    if (Object.keys(next).length > 0) {
      setErrors(next);
      return;
    }

    setErrors({});
    setSaving(true);
    setSaved(false);

    try {
      const updated = await apiFetch<UserResponse>("/api/users/me", {
        method: "PATCH",
        body: JSON.stringify({ displayName: displayName.trim() }),
      });
      onUpdate(updated);
      setSaved(true);
    } catch (err) {
      const apiError = err as ApiError;
      setErrors({ form: apiError.error || "Failed to update profile." });
    } finally {
      setSaving(false);
    }
  }

  return (
    <section>
      <h2 className="text-lg font-semibold text-gray-900 mb-4">Profile</h2>
      <form onSubmit={handleSave} className="space-y-4">
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
            htmlFor="displayName"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Display name
          </label>
          <input
            id="displayName"
            type="text"
            value={displayName}
            onChange={(e) => {
              setDisplayName(e.target.value);
              setSaved(false);
              if (errors.displayName)
                setErrors({ ...errors, displayName: undefined });
            }}
            aria-invalid={!!errors.displayName}
            aria-describedby={
              errors.displayName ? "displayName-error" : undefined
            }
            className={`w-full rounded-lg border px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-1 ${
              errors.displayName
                ? "border-red-400 focus:border-red-500 focus:ring-red-500"
                : "border-gray-200 focus:border-primary-500 focus:ring-primary-500"
            }`}
          />
          {errors.displayName && (
            <p id="displayName-error" className="mt-1 text-xs text-red-600">
              {errors.displayName}
            </p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <div className="flex items-center gap-2">
            <input
              type="email"
              value={user.email}
              disabled
              className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-500 shadow-sm"
            />
            {user.emailVerified ? (
              <span className="shrink-0 rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-700">
                Verified
              </span>
            ) : (
              <span className="shrink-0 rounded-full bg-amber-100 px-2.5 py-0.5 text-xs font-medium text-amber-700">
                Unverified
              </span>
            )}
          </div>
        </div>

        <div className="flex items-center gap-3">
          <button
            type="submit"
            disabled={saving}
            className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {saving ? "Saving..." : "Save"}
          </button>
          {saved && (
            <span className="text-sm text-green-600">Profile updated</span>
          )}
        </div>
      </form>
    </section>
  );
}

function ChangePasswordSection() {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState<PasswordErrors>({});
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);

  function validate(): PasswordErrors {
    const next: PasswordErrors = {};
    if (!currentPassword)
      next.currentPassword = "Current password is required";
    if (!newPassword) next.newPassword = "New password is required";
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
    setSaving(true);
    setSuccess(false);

    try {
      await apiFetch("/api/users/me/change-password", {
        method: "POST",
        body: JSON.stringify({ currentPassword, newPassword }),
      });
      setSuccess(true);
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      const apiError = err as ApiError;
      setErrors({
        form: apiError.error || "Failed to change password.",
      });
    } finally {
      setSaving(false);
    }
  }

  return (
    <section>
      <h2 className="text-lg font-semibold text-gray-900 mb-4">
        Change password
      </h2>
      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        {errors.form && (
          <div
            role="alert"
            className="rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600"
          >
            {errors.form}
          </div>
        )}
        {success && (
          <div className="rounded-lg bg-green-50 border border-green-200 p-3 text-sm text-green-600">
            Password updated successfully.
          </div>
        )}

        <div>
          <label
            htmlFor="currentPassword"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Current password
          </label>
          <input
            id="currentPassword"
            type="password"
            autoComplete="current-password"
            value={currentPassword}
            onChange={(e) => {
              setCurrentPassword(e.target.value);
              setSuccess(false);
              if (errors.currentPassword)
                setErrors({ ...errors, currentPassword: undefined });
            }}
            aria-invalid={!!errors.currentPassword}
            aria-describedby={
              errors.currentPassword ? "currentPassword-error" : undefined
            }
            className={`w-full rounded-lg border px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-1 ${
              errors.currentPassword
                ? "border-red-400 focus:border-red-500 focus:ring-red-500"
                : "border-gray-200 focus:border-primary-500 focus:ring-primary-500"
            }`}
          />
          {errors.currentPassword && (
            <p
              id="currentPassword-error"
              className="mt-1 text-xs text-red-600"
            >
              {errors.currentPassword}
            </p>
          )}
        </div>

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
              setSuccess(false);
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
            Confirm new password
          </label>
          <input
            id="confirmPassword"
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            onChange={(e) => {
              setConfirmPassword(e.target.value);
              setSuccess(false);
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
            placeholder="Repeat your new password"
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
          disabled={saving}
          className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50"
        >
          {saving ? "Updating..." : "Update password"}
        </button>
      </form>
    </section>
  );
}

function NotificationsSection({
  user,
  onUpdate,
}: {
  user: UserResponse;
  onUpdate: (updated: UserResponse) => void;
}) {
  const [enabled, setEnabled] = useState(user.remindersEnabled);
  const [saving, setSaving] = useState(false);

  const handleToggle = useCallback(async () => {
    const newValue = !enabled;
    setEnabled(newValue);
    setSaving(true);
    try {
      const updated = await apiFetch<UserResponse>("/api/users/me/reminders", {
        method: "PATCH",
        body: JSON.stringify({ enabled: newValue }),
      });
      onUpdate(updated);
    } catch {
      setEnabled(!newValue);
    } finally {
      setSaving(false);
    }
  }, [enabled, onUpdate]);

  return (
    <section>
      <h2 className="text-lg font-semibold text-gray-900 mb-4">
        Notifications
      </h2>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-700">Sunday reminders</p>
          <p className="text-xs text-gray-500">
            Receive a weekly email reminder to complete your check-in
          </p>
        </div>
        <button
          onClick={handleToggle}
          disabled={saving}
          role="switch"
          aria-checked={enabled}
          className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 ${
            enabled ? "bg-primary-600" : "bg-gray-200"
          }`}
        >
          <span
            className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
              enabled ? "translate-x-6" : "translate-x-1"
            }`}
          />
        </button>
      </div>
    </section>
  );
}

function getSubscriptionLabel(status: string): string {
  switch (status) {
    case "ACTIVE":
      return "Pro";
    case "CANCELED":
      return "Pro (Canceled)";
    case "PAST_DUE":
      return "Pro (Past Due)";
    default:
      return "Free";
  }
}

function SubscriptionSection({ user }: { user: UserResponse }) {
  const label = getSubscriptionLabel(user.subscriptionStatus);
  const isActive = user.subscriptionStatus === "ACTIVE";

  return (
    <section id="subscription">
      <h2 className="text-lg font-semibold text-gray-900 mb-4">
        Subscription
      </h2>
      <div className="flex items-center justify-between mb-4">
        <div>
          <p className="text-sm text-gray-700">
            Current plan:{" "}
            <span className="font-medium text-gray-900">{label}</span>
          </p>
        </div>
        {isActive && (
          <span className="rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-700">
            Active
          </span>
        )}
      </div>
      {!isActive && (
        <div className="mt-2">
          <p className="text-sm text-gray-500 mb-4">
            Upgrade for unlimited weekly reflections and AI insights.
          </p>
          <PricingCards />
        </div>
      )}
    </section>
  );
}

export default function AccountPage() {
  const { user, isLoading, logout } = useAuth();
  const router = useRouter();
  const [currentUser, setCurrentUser] = useState<UserResponse | null>(null);

  // Sync user from auth context into local state for optimistic updates
  const displayUser = currentUser ?? user;

  async function handleSignOut() {
    await logout();
    router.push("/login");
  }

  if (isLoading) {
    return <Spinner label="Loading..." />;
  }

  if (!displayUser) {
    router.push("/login");
    return null;
  }

  return (
    <div className="flex items-start justify-center px-4 pt-10 pb-16">
      <div className="w-full max-w-xl space-y-8">
        <h1 className="text-2xl font-semibold tracking-tight">Account</h1>

        <ProfileSection
          user={displayUser}
          onUpdate={(updated) => setCurrentUser(updated)}
        />

        <hr className="border-gray-200" />

        <ChangePasswordSection />

        <hr className="border-gray-200" />

        <NotificationsSection
          user={displayUser}
          onUpdate={(updated) => setCurrentUser(updated)}
        />

        <hr className="border-gray-200" />

        <SubscriptionSection user={displayUser} />

        <hr className="border-gray-200" />

        <section>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Sign out
          </h2>
          <p className="text-sm text-gray-500 mb-4">
            Sign out of your account on this device.
          </p>
          <button
            onClick={handleSignOut}
            className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2"
          >
            Sign out
          </button>
        </section>
      </div>
    </div>
  );
}
