import { Link, useLocation } from 'react-router-dom';
import { CheckCircle2 } from 'lucide-react';
import { Button } from '../components/Button.jsx';

export function PaymentSuccess() {
  const { state } = useLocation();
  const order = state?.order;

  return (
    <section className="mx-auto grid min-h-[70vh] max-w-2xl content-center px-4 py-10 text-center">
      <div className="rounded-lg bg-white p-8 shadow-soft dark:bg-white/5">
        <CheckCircle2 className="mx-auto text-emerald-600" size={56} />
        <h1 className="mt-4 text-3xl font-black">Payment successful</h1>
        <p className="mt-3 text-black/60 dark:text-white/65">
          Your SwiftCart order has been placed and payment has been verified securely.
        </p>
        {order?.orderNumber && <div className="mt-5 rounded-md bg-emerald-50 p-3 text-sm font-black text-emerald-700">Order {order.orderNumber}</div>}
        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <Link to="/orders"><Button>View orders</Button></Link>
          <Link to="/products"><Button className="bg-black/10 text-ink dark:bg-white/10 dark:text-white">Continue shopping</Button></Link>
        </div>
      </div>
    </section>
  );
}

