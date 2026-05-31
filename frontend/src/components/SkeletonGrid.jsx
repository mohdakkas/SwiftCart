export function SkeletonGrid() {
  return (
    <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
      {Array.from({ length: 8 }).map((_, index) => (
        <div key={index} className="rounded-lg border border-black/10 bg-white p-4 dark:border-white/10 dark:bg-white/5">
          <div className="shimmer aspect-[4/3] rounded bg-black/10" />
          <div className="shimmer mt-4 h-4 w-3/4 rounded bg-black/10" />
          <div className="shimmer mt-3 h-4 w-1/2 rounded bg-black/10" />
        </div>
      ))}
    </div>
  );
}
