"use client";

import Link from "next/link";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";

export default function Header() {
  const { user, logout } = useAuth();
  const router = useRouter();

  async function handleLogout() {
    await logout();
    router.push("/login");
  }

  return (
    <header className="border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-14 max-w-3xl items-center justify-between px-4">
        <Link
          href={user ? "/check-in" : "/"}
          className="text-lg font-semibold tracking-tight text-gray-900"
        >
          Reflect
        </Link>

        {user && (
          <nav className="flex items-center gap-5">
            <Link
              href="/check-in"
              className="text-sm text-gray-500 hover:text-gray-900"
            >
              Check-in
            </Link>
            <Link
              href="/history"
              className="text-sm text-gray-500 hover:text-gray-900"
            >
              History
            </Link>
            <Link
              href="/account"
              className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900"
            >
              {user.displayName}
              {user.pro && (
                <span className="rounded-full bg-primary-100 px-1.5 py-0.5 text-[10px] font-semibold uppercase tracking-wide text-primary-700">
                  Pro
                </span>
              )}
            </Link>
            <button
              onClick={handleLogout}
              className="text-sm text-gray-500 hover:text-gray-900"
            >
              Sign out
            </button>
          </nav>
        )}
      </div>
    </header>
  );
}
