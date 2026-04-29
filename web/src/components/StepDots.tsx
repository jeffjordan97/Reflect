import { Check } from "lucide-react";

interface StepDotsProps {
  total: number;
  current: number;
}

export default function StepDots({ total, current }: StepDotsProps) {
  return (
    <div className="flex items-center justify-center gap-1.5">
      {Array.from({ length: total }, (_, i) => (
        <div key={i} className="flex items-center gap-1.5">
          <div
            className={`flex items-center justify-center rounded-full transition-colors duration-300 ${
              i < current
                ? "h-2.5 w-2.5 bg-primary-400 md:h-3 md:w-3"
                : i === current
                  ? "h-2.5 w-2.5 bg-primary-400/20 ring-2 ring-primary-400 md:h-3 md:w-3"
                  : "h-2.5 w-2.5 bg-border-default md:h-3 md:w-3"
            }`}
          >
            {i < current && <Check size={8} strokeWidth={3} className="text-white hidden md:block" />}
          </div>
          {i < total - 1 && (
            <div
              className={`h-0.5 w-4 rounded-full transition-colors duration-500 md:w-6 ${
                i < current ? "bg-primary-400" : "bg-border-default"
              }`}
            />
          )}
        </div>
      ))}
    </div>
  );
}
