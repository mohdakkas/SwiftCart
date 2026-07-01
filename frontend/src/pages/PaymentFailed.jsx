import { Link, useLocation } from 'react-router-dom';
import { XCircle } from 'lucide-react';
import { Button } from '../components/Button.jsx';

export function PaymentFailed() {
  const { state } = useLocation();
  const message = state?.message ?? 'Payment was not completed. Your cart is still available so you can try again.';

  return (
    <section className="mx-auto grid min-h-[70vh] max-w-2xl content-center px-4 py-10 text-center">
      <div className="rounded-lg bg-white p-8 shadow-soft dark:bg-white/5">
        <XCircle className="mx-auto text-coral" size={56} />
        <h1 className="mt-4 text-3xl font-black">Payment failed</h1>
        <p className="mt-3 text-black/60 dark:text-white/65">{message}</p>
        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <Link to="/checkout"><Button>Try again</Button></Link>
          <Link to="/cart"><Button className="bg-black/10 text-ink dark:bg-white/10 dark:text-white">Back to cart</Button></Link>
        </div>
      </div>
    </section>
  );
}

