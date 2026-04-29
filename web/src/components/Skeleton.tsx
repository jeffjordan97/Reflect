interface SkeletonProps {
  className?: string;
}

export default function Skeleton({ className }: SkeletonProps) {
  return (
    <div
      className={`animate-shimmer rounded-card bg-gradient-to-r from-border-default via-slate-100 to-border-default bg-[length:200%_100%] ${className ?? ""}`}
    />
  );
}
