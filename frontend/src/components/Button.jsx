export function Button({ children, className = '', ...props }) {
  return (
    <button
      className={`inline-flex items-center justify-center gap-2 rounded-md bg-ink px-4 py-2 text-sm font-bold text-white transition hover:opacity-90 disabled:opacity-50 dark:bg-white dark:text-ink ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
