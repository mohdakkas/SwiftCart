import { Button } from '../components/Button.jsx';

export function Addresses() {
  return (
    <section className="mx-auto max-w-4xl px-4 py-10">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-3xl font-black">Addresses</h1>
        <Button>Add address</Button>
      </div>
      <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
        <div className="font-black">Home</div>
        <p className="mt-1 text-sm text-black/60 dark:text-white/60">42 MG Road, Bengaluru, Karnataka 560001</p>
      </div>
    </section>
  );
}
