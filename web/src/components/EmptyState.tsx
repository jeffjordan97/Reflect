import type { ReactNode } from "react";

interface EmptyStateProps {
  icon: ReactNode;
  heading: string;
  description: string;
  action?: ReactNode;
}

export default function EmptyState({ icon, heading, description, action }: EmptyStateProps) {
  return (
    <div className="flex min-h-[300px] flex-col items-center justify-center gap-3 rounded-xl border border-dashed border-border-default p-8 text-center">
      <div className="rounded-full bg-slate-100 p-4 text-text-muted">
        {icon}
      </div>
      <h3 className="font-medium text-text-primary">{heading}</h3>
      <p className="max-w-sm text-sm text-text-secondary">{description}</p>
      {action}
    </div>
  );
}
