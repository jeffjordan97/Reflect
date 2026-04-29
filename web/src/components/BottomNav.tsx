"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { PenLine, Clock, Sparkles, User } from "lucide-react";

const tabs = [
  { href: "/check-in", label: "Check-in", icon: PenLine },
  { href: "/history", label: "History", icon: Clock },
  { href: "/insights", label: "Insights", icon: Sparkles },
  { href: "/account", label: "Account", icon: User },
] as const;

export default function BottomNav() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 border-t border-border-default bg-surface/90 backdrop-blur-sm pb-[env(safe-area-inset-bottom)] md:hidden">
      <div className="flex items-center justify-around py-2">
        {tabs.map(({ href, label, icon: Icon }) => {
          const active = pathname.startsWith(href);
          return (
            <Link
              key={href}
              href={href}
              className={`flex flex-col items-center gap-0.5 px-3 py-1 text-[11px] ${
                active
                  ? "text-primary-400 border-t-2 border-primary-400 -mt-[2px]"
                  : "text-text-muted"
              }`}
            >
              <Icon size={20} strokeWidth={active ? 2 : 1.5} />
              <span>{label}</span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
