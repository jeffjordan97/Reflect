"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { useScrolled } from "@/hooks/useScrolled";
import { apiFetch } from "@/lib/api";

export default function Header() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const scrolled = useScrolled(10);
  const [streak, setStreak] = useState<number | null>(null);

  useEffect(() => {
    if (!user) return;
    apiFetch<{ streak: number }>("/api/check-ins/streak")
      .then((data) => setStreak(data.streak))
      .catch(() => setStreak(null));
  }, [user]);

  async function handleLogout() {
    await logout();
    router.push("/login");
  }

  return (
    <header
      className={`sticky top-0 z-50 w-full transition-all duration-300 ${
        scrolled
          ? "bg-canvas/80 backdrop-blur-md shadow-sm border-b border-border-default"
          : "bg-transparent"
      }`}
    >
      <div className="mx-auto flex h-14 max-w-3xl items-center justify-between px-4">
        <Link href={user ? "/check-in" : "/"} className="font-serif text-lg font-semibold text-text-primary">
          Reflect
        </Link>

        {user && (
          <nav className="hidden items-center gap-5 md:flex">
            <Link href="/check-in" className="text-sm text-text-secondary hover:text-text-primary">
              Check-in
            </Link>
            <Link href="/history" className="text-sm text-text-secondary hover:text-text-primary">
              History
            </Link>
            <Link href="/account" className="flex items-center gap-2 text-sm text-text-secondary hover:text-text-primary">
              {user.displayName}
              {user.pro && (
                <span className="rounded-badge bg-primary-100 px-1.5 py-0.5 text-[10px] font-semibold uppercase tracking-wide text-primary-700">
                  Pro
                </span>
              )}
            </Link>
            {streak !== null && streak > 0 && (
              <span className="flex items-center gap-1.5 rounded-badge bg-amber-50 px-2.5 py-0.5 text-xs font-semibold text-amber-800">
                <span className="inline-block h-2 w-2 rounded-full bg-primary-400" />
                {streak}
              </span>
            )}
            <button onClick={handleLogout} className="text-sm text-text-secondary hover:text-text-primary">
              Sign out
            </button>
          </nav>
        )}
      </div>
    </header>
  );
}
