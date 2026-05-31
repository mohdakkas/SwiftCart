export function StatCard({ label, value }) {
  return (
    <div className="rounded-lg border border-black/10 bg-white p-5 shadow-sm">
      <div className="text-sm font-semibold text-black/55">{label}</div>
      <div className="mt-2 text-3xl font-black">{value}</div>
    </div>
  );
}
