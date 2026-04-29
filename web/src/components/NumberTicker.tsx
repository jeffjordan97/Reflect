"use client";

import { useEffect, useState } from "react";

interface NumberTickerProps {
  value: number;
  duration?: number;
  className?: string;
}

export default function NumberTicker({ value, duration = 500, className }: NumberTickerProps) {
  const [display, setDisplay] = useState(0);

  useEffect(() => {
    const startTime = performance.now();
    let rafId: number;

    function tick(now: number) {
      const progress = Math.min((now - startTime) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      setDisplay(Math.round(eased * value));
      if (progress < 1) {
        rafId = requestAnimationFrame(tick);
      }
    }

    rafId = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(rafId);
  }, [value, duration]);

  return <span className={className}>{display}</span>;
}
