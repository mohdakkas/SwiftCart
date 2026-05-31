import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { cartService } from '../services/cartService.js';
import { Button } from '../components/Button.jsx';

export function Cart() {
  const { data } = useQuery({ queryKey: ['cart'], queryFn: cartService.get, retry: false });
  const cart = data ?? { items: [], subtotal: 0, tax: 0, shipping: 0, total: 0 };
  return (
    <section className="mx-auto grid max-w-7xl gap-6 px-4 py-10 lg:grid-cols-[1fr_360px]">
      <div>
        <h1 className="mb-6 text-3xl font-black">Shopping cart</h1>
        <div className="grid gap-3">
          {cart.items.length === 0 && <div className="rounded-lg bg-white p-6 dark:bg-white/5">Your cart is empty.</div>}
          {cart.items.map((item) => (
            <article key={item.id} className="flex items-center justify-between rounded-lg bg-white p-4 dark:bg-white/5">
              <div><div className="font-bold">{item.productName}</div><div className="text-sm text-black/60">Qty {item.quantity}</div></div>
              <div className="font-black">₹{Number(item.lineTotal).toLocaleString('en-IN')}</div>
            </article>
          ))}
        </div>
      </div>
      <aside className="h-fit rounded-lg bg-white p-5 shadow-soft dark:bg-white/5">
        <h2 className="text-xl font-black">Order summary</h2>
        {['subtotal', 'tax', 'shipping', 'total'].map((key) => <div key={key} className="mt-3 flex justify-between capitalize"><span>{key}</span><strong>₹{Number(cart[key]).toLocaleString('en-IN')}</strong></div>)}
        <Link to="/checkout"><Button className="mt-5 w-full">Checkout</Button></Link>
      </aside>
    </section>
  );
}
