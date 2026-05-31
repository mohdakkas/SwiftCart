import { CreditCard, MapPin } from 'lucide-react';
import { Button } from '../components/Button.jsx';

export function Checkout() {
  return (
    <section className="mx-auto max-w-5xl px-4 py-10">
      <h1 className="text-3xl font-black">Checkout</h1>
      <div className="mt-6 grid gap-5 md:grid-cols-2">
        <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
          <MapPin className="mb-3 text-signal" />
          <h2 className="text-xl font-black">Shipping address</h2>
          <p className="mt-2 text-sm text-black/60 dark:text-white/60">Use saved addresses or add a new delivery location.</p>
        </div>
        <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
          <CreditCard className="mb-3 text-signal" />
          <h2 className="text-xl font-black">Payment method</h2>
          <div className="mt-4 grid gap-2 text-sm">
            {['Cash On Delivery', 'UPI', 'Credit Card', 'Debit Card', 'Net Banking', 'Wallet'].map((method) => <label key={method} className="flex gap-2"><input name="payment" type="radio" /> {method}</label>)}
          </div>
        </div>
      </div>
      <Button className="mt-6">Place order</Button>
    </section>
  );
}
