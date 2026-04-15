"use client";

import { Slider } from "@ark-ui/react/slider";

interface EnergySliderProps {
  value: number;
  onChange: (value: number) => void;
}

const energyLabels: Record<number, string> = {
  1: "Exhausted", 2: "Very low", 3: "Low", 4: "Below average", 5: "Neutral",
  6: "Above average", 7: "Good", 8: "High", 9: "Very high", 10: "Peak",
};

function energyColor(value: number): string {
  if (value <= 3) return "bg-red-500";
  if (value <= 5) return "bg-amber-500";
  if (value <= 7) return "bg-emerald-400";
  return "bg-emerald-500";
}

export default function EnergySlider({ value, onChange }: EnergySliderProps) {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <span className="text-4xl font-semibold text-gray-900">{value}</span>
        <span className="text-sm text-gray-500">{energyLabels[value]}</span>
      </div>
      <Slider.Root min={1} max={10} step={1} value={[value]} onValueChange={(details) => onChange(details.value[0])}>
        <Slider.Control className="relative flex items-center h-6">
          <Slider.Track className="relative h-2 w-full rounded-full bg-gray-200">
            <Slider.Range className={`absolute h-full rounded-full ${energyColor(value)}`} />
          </Slider.Track>
          <Slider.Thumb index={0}
            className="block h-5 w-5 rounded-full border-2 border-primary-600 bg-white shadow-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 cursor-grab active:cursor-grabbing" />
        </Slider.Control>
      </Slider.Root>
      <div className="flex justify-between text-xs text-gray-400">
        <span>1</span><span>5</span><span>10</span>
      </div>
    </div>
  );
}
