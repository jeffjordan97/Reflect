"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, CheckInRequest } from "@/lib/types";
import EnergySlider from "./EnergySlider";

interface WizardProps {
  existing?: CheckInResponse | null;
}

const STEPS = [
  { key: "wins", label: "Wins", prompt: "What moved forward this week?" },
  { key: "friction", label: "Friction", prompt: "Where did you feel resistance?" },
  { key: "energyRating", label: "Energy", prompt: "How was your energy this week?" },
  { key: "signalMoment", label: "Signal Moment", prompt: "What interaction or moment is still on your mind?" },
  { key: "intentions", label: "Intentions", prompt: "What matters most next week?" },
] as const;

export default function CheckInWizard({ existing }: WizardProps) {
  const router = useRouter();
  const [step, setStep] = useState(0);
  const [checkInId, setCheckInId] = useState<string | null>(existing?.id ?? null);
  const [isSaving, setIsSaving] = useState(false);

  const [wins, setWins] = useState(existing?.wins ?? "");
  const [friction, setFriction] = useState(existing?.friction ?? "");
  const [energyRating, setEnergyRating] = useState(existing?.energyRating ?? 5);
  const [signalMoment, setSignalMoment] = useState(existing?.signalMoment ?? "");
  const [intentions, setIntentions] = useState(existing?.intentions ?? "");

  function getCurrentValue(): string | number {
    switch (step) {
      case 0: return wins;
      case 1: return friction;
      case 2: return energyRating;
      case 3: return signalMoment;
      case 4: return intentions;
      default: return "";
    }
  }

  function buildRequest(): CheckInRequest {
    return {
      wins: wins || undefined,
      friction: friction || undefined,
      energyRating: energyRating,
      signalMoment: signalMoment || undefined,
      intentions: intentions || undefined,
      completed: step === STEPS.length - 1 ? true : undefined,
    };
  }

  async function saveProgress() {
    setIsSaving(true);
    try {
      const request = buildRequest();
      if (!checkInId) {
        const created = await apiFetch<CheckInResponse>("/api/check-ins", {
          method: "POST",
          body: JSON.stringify(request),
        });
        setCheckInId(created.id);
      } else {
        await apiFetch<CheckInResponse>(`/api/check-ins/${checkInId}`, {
          method: "PUT",
          body: JSON.stringify(request),
        });
      }
    } catch {
      // Save failed — user can retry
    } finally {
      setIsSaving(false);
    }
  }

  async function handleNext() {
    await saveProgress();
    if (step < STEPS.length - 1) {
      setStep(step + 1);
    } else {
      router.push("/check-in");
      router.refresh();
    }
  }

  function handleBack() {
    if (step > 0) setStep(step - 1);
  }

  const currentStep = STEPS[step];
  const isLastStep = step === STEPS.length - 1;
  const progress = ((step + 1) / STEPS.length) * 100;

  return (
    <div className="mx-auto w-full max-w-xl px-4 py-8">
      <div className="mb-8">
        <div className="flex justify-between text-xs text-gray-400 mb-2">
          <span>Step {step + 1} of {STEPS.length}</span>
          <span>{currentStep.label}</span>
        </div>
        <div className="h-1.5 w-full rounded-full bg-gray-100">
          <div className="h-full rounded-full bg-primary-600 transition-all duration-300" style={{ width: `${progress}%` }} />
        </div>
      </div>
      <h2 className="text-xl font-semibold text-gray-900 mb-6">{currentStep.prompt}</h2>
      <div className="mb-8">
        {step === 2 ? (
          <EnergySlider value={energyRating} onChange={setEnergyRating} />
        ) : (
          <textarea
            value={getCurrentValue() as string}
            onChange={(e) => {
              const val = e.target.value;
              switch (step) {
                case 0: setWins(val); break;
                case 1: setFriction(val); break;
                case 3: setSignalMoment(val); break;
                case 4: setIntentions(val); break;
              }
            }}
            rows={6}
            className="w-full rounded-lg border border-gray-200 px-4 py-3 text-sm shadow-sm placeholder:text-gray-400 focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500 resize-none"
            placeholder="Take your time..."
          />
        )}
      </div>
      <div className="flex justify-between">
        {step > 0 ? (
          <button onClick={handleBack}
            className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50">
            Back
          </button>
        ) : <div />}
        <button onClick={handleNext} disabled={isSaving}
          className="rounded-lg bg-primary-600 px-6 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50">
          {isSaving ? "Saving..." : isLastStep ? "Complete" : "Next"}
        </button>
      </div>
    </div>
  );
}
