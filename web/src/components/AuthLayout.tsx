import type { ReactNode } from "react";

interface AuthLayoutProps {
  children: ReactNode;
}

export default function AuthLayout({ children }: AuthLayoutProps) {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="px-4 py-6">
        <div className="mx-auto max-w-sm">
          <span className="text-lg font-semibold tracking-tight text-gray-900">
            Reflect
          </span>
        </div>
      </header>
      <div className="flex-1 flex items-start justify-center px-4 pt-8 pb-16">
        {children}
      </div>
    </div>
  );
}
