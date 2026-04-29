interface FloatingInputProps {
  id: string;
  label: string;
  type?: string;
  autoComplete?: string;
  value: string;
  onChange: (value: string) => void;
  error?: string;
  hint?: string;
}

export default function FloatingInput({
  id,
  label,
  type = "text",
  autoComplete,
  value,
  onChange,
  error,
  hint,
}: FloatingInputProps) {
  const hasError = !!error;

  return (
    <div>
      <div className="relative">
        <input
          id={id}
          type={type}
          autoComplete={autoComplete}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder=" "
          aria-invalid={hasError}
          aria-describedby={error ? `${id}-error` : hint ? `${id}-hint` : undefined}
          className={`peer w-full rounded-input border bg-surface px-4 pb-2 pt-6 text-sm text-text-primary shadow-sm transition-colors focus:outline-none focus:ring-1 ${
            hasError
              ? "border-red-400 focus:border-red-500 focus:ring-red-500"
              : "border-border-input focus:border-primary-400 focus:ring-primary-400"
          }`}
        />
        <label
          htmlFor={id}
          className="pointer-events-none absolute left-4 top-4 origin-top-left text-sm text-text-muted transition-all duration-200 peer-placeholder-shown:translate-y-0 peer-placeholder-shown:scale-100 peer-focus:-translate-y-3 peer-focus:scale-[0.85] peer-focus:text-primary-400 peer-[:not(:placeholder-shown)]:-translate-y-3 peer-[:not(:placeholder-shown)]:scale-[0.85]"
        >
          {label}
        </label>
      </div>
      {error && (
        <p id={`${id}-error`} className="mt-1 text-xs text-red-600">{error}</p>
      )}
      {!error && hint && (
        <p id={`${id}-hint`} className="mt-1 text-xs text-text-muted">{hint}</p>
      )}
    </div>
  );
}
