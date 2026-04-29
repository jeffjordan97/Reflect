import type { ReactNode } from "react";

interface AuthLayoutProps {
  children: ReactNode;
}

export default function AuthLayout({ children }: AuthLayoutProps) {
  return (
    <div className="grid min-h-[calc(100vh-56px)] lg:grid-cols-2">
      {/* Brand panel — desktop only */}
      <div className="hidden lg:flex flex-col justify-between bg-gradient-to-br from-primary-900 to-slate-900 p-10 text-white">
        <span className="font-serif text-2xl font-semibold">Reflect</span>
        <div>
          <p className="text-lg italic leading-relaxed opacity-80">
            &ldquo;The weekly pause that made me actually notice my patterns.&rdquo;
          </p>
          <p className="mt-2 text-sm opacity-50">— Early beta user</p>
        </div>
      </div>

      {/* Form panel */}
      <div className="flex items-center justify-center bg-canvas px-4 py-12">
        <div className="w-full max-w-sm">
          {children}
        </div>
      </div>
    </div>
  );
}
