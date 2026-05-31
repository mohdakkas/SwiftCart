import { Package, ShieldCheck, Truck } from 'lucide-react';

export function UserDashboard() {
  return (
    <section className="mx-auto max-w-7xl px-4 py-10">
      <h1 className="text-3xl font-black">Dashboard</h1>
      <div className="mt-6 grid gap-4 md:grid-cols-3">
        {[['Orders in progress', '3', Truck], ['Wishlist items', '12', Package], ['Secure account', 'Active', ShieldCheck]].map(([label, value, Icon]) => (
          <div key={label} className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
            <Icon className="mb-4 text-signal" />
            <div className="text-sm text-black/60 dark:text-white/60">{label}</div>
            <div className="text-2xl font-black">{value}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
