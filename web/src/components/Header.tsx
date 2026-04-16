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
          <nav className="flex items-center gap-6">
            <Link
              href="/history"
              className="text-sm text-gray-500 hover:text-gray-900"
            >
              History
            </Link>
            <Link
              href="/account"
              className="text-sm text-gray-500 hover:text-gray-900"
            >
              {user.displayName}
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
