import { Star } from 'lucide-react';

export function StarRating({ value = 0, onChange, size = 20, readOnly = false, className = '' }) {
  const rounded = Math.round(Number(value) || 0);

  return (
    <div className={`inline-flex items-center gap-1 ${className}`}>
      {[1, 2, 3, 4, 5].map((star) => {
        const active = star <= rounded;
        const baseClass = active ? 'fill-amber-400 text-amber-400' : 'text-black/25 dark:text-white/25';
        if (readOnly) {
          return <Star key={star} size={size} className={baseClass} aria-hidden="true" />;
        }
        return (
          <button
            key={star}
            type="button"
            aria-label={`${star} star${star > 1 ? 's' : ''}`}
            onClick={() => onChange?.(star)}
            className="rounded p-0.5 transition hover:scale-110 focus:outline-none focus:ring-2 focus:ring-signal"
          >
            <Star size={size} className={baseClass} />
          </button>
        );
      })}
    </div>
  );
}
