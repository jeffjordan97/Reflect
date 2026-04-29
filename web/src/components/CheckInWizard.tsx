"use client";

import { useEffect, useState } from "react";
import { Transition } from "@headlessui/react";
import { apiFetch } from "@/lib/api";
import type { CheckInResponse, CheckInRequest } from "@/lib/types";
import EnergySlider from "./EnergySlider";
import StepDots from "./StepDots";
import CompletionMoment from "./CompletionMoment";

interface WizardProps {
  existing?: CheckInResponse | null;
}

const STEPS = [
  { key: "wins", label: "Progress", prompt: "What moved forward this week?", subtitle: "In your work or how you worked with others" },
  { key: "friction", label: "Friction", prompt: "Where did you feel resistance?", subtitle: "A task, a person, or yourself" },
  { key: "energyRating", label: "Energy", prompt: "How was your energy this week?", subtitle: "Rate from 1 (exhausted) to 10 (peak)" },
  { key: "signalMoment", label: "Signal Moment", prompt: "What interaction is still on your mind?", subtitle: "And why does it linger?" },
  { key: "intentions", label: "Intentions", prompt: "What matters most next week?", subtitle: "And why does it matter?" },
] as const;

export default function CheckInWizard({ existing }: WizardProps) {
  const [step, setStep] = useState(0);
  const [direction, setDirection] = useState<"forward" | "backward">("forward");
  const [showStep, setShowStep] = useState(true);
  const [checkInId, setCheckInId] = useState<string | null>(existing?.id ?? null);
  const [isSaving, setIsSaving] = useState(false);
  const [completed, setCompleted] = useState(false);
  const [streak, setStreak] = useState(0);

  const [wins, setWins] = useState(existing?.wins ?? "");
  const [friction, setFriction] = useState(existing?.friction ?? "");
  const [energyRating, setEnergyRating] = useState(existing?.energyRating ?? 5);
  const [signalMoment, setSignalMoment] = useState(existing?.signalMoment ?? "");
  const [intentions, setIntentions] = useState(existing?.intentions ?? "");

  useEffect(() => {
    apiFetch<{ streak: number }>("/api/check-ins/streak")
      .then((data) => setStreak(data.streak))
      .catch(() => setStreak(0));
  }, []);

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
      energyRating,
      signalMoment: signalMoment || undefined,
      intentions: intentions || undefined,
      completed: step === STEPS.length - 1 ? true : undefined,
    };
  }

  async function saveProgress(): Promise<string | null> {
    setIsSaving(true);
    try {
      const request = buildRequest();
      if (!checkInId) {
        const created = await apiFetch<CheckInResponse>("/api/check-ins", {
          method: "POST",
          body: JSON.stringify(request),
        });
        setCheckInId(created.id);
        return created.id;
      } else {
        await apiFetch<CheckInResponse>(`/api/check-ins/${checkInId}`, {
          method: "PUT",
          body: JSON.stringify(request),
        });
        return checkInId;
      }
    } catch {
      return null;
    } finally {
      setIsSaving(false);
    }
  }

  async function handleNext() {
    const savedId = await saveProgress();
    if (step < STEPS.length - 1) {
      setDirection("forward");
      setShowStep(false);
      setTimeout(() => {
        setStep(step + 1);
        setShowStep(true);
      }, 200);
    } else if (savedId) {
      setCompleted(true);
    }
  }

  function handleBack() {
    if (step > 0) {
      setDirection("backward");
      setShowStep(false);
      setTimeout(() => {
        setStep(step - 1);
        setShowStep(true);
      }, 200);
    }
  }

  if (completed && checkInId) {
    return (
      <div className="mx-auto w-full max-w-xl px-4 py-8">
        <CompletionMoment checkInId={checkInId} streak={streak + 1} />
      </div>
    );
  }

  const currentStep = STEPS[step];
  const isLastStep = step === STEPS.length - 1;

  return (
    <div className="mx-auto flex min-h-[60vh] w-full max-w-xl flex-col items-center justify-center px-4 py-8">
      <div className="mb-7 w-full">
        <StepDots total={STEPS.length} current={step} />
      </div>

      <Transition
        show={showStep}
        enter={`transition-all duration-300 ease-out`}
        enterFrom={direction === "forward" ? "translate-x-8 opacity-0" : "-translate-x-8 opacity-0"}
        enterTo="translate-x-0 opacity-100"
        leave="transition-all duration-200 ease-in"
        leaveFrom="translate-x-0 opacity-100"
        leaveTo={direction === "forward" ? "-translate-x-8 opacity-0" : "translate-x-8 opacity-0"}
      >
        <div className="w-full">
        <div className="rounded-2xl border border-border-default bg-surface p-6 shadow-sm md:p-8">
          <p className="font-mono text-xs uppercase tracking-wider text-text-muted">
            Step {step + 1} of {STEPS.length}
          </p>
          <p className="mt-1 text-xs font-medium text-primary-400">{currentStep.label}</p>

          <h2 className="mt-4 font-serif text-xl font-semibold text-text-primary md:text-[22px]">
            {currentStep.prompt}
          </h2>
          <p className="mt-1 text-sm text-text-secondary">{currentStep.subtitle}</p>

          <div className="mt-6">
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
                rows={5}
                className="w-full rounded-xl border border-border-input bg-canvas px-4 py-3 text-sm text-text-primary shadow-sm placeholder:text-text-muted focus:border-primary-400 focus:outline-none focus:ring-1 focus:ring-primary-400 resize-none"
                placeholder="Take your time..."
              />
            )}
          </div>

          <div className="mt-6 flex justify-between gap-3">
            {step > 0 ? (
              <button
                onClick={handleBack}
                className="rounded-input border border-border-default px-4 py-2.5 text-sm font-medium text-text-secondary shadow-sm hover:bg-slate-50"
              >
                Back
              </button>
            ) : (
              <div />
            )}
            <button
              onClick={handleNext}
              disabled={isSaving}
              className="rounded-input bg-primary-400 px-6 py-2.5 text-sm font-semibold text-primary-900 shadow-sm hover:bg-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:ring-offset-2 disabled:opacity-50"
            >
              {isSaving ? "Saving..." : isLastStep ? "Complete" : "Continue"}
            </button>
          </div>
        </div>
        </div>
      </Transition>
    </div>
  );
}
