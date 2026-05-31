import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { orderService } from '../services/orderService.js';

export function Orders() {
  const { data } = useQuery({ queryKey: ['orders'], queryFn: orderService.history, retry: false });
  const orders = data?.content ?? [];
  return (
    <section className="mx-auto max-w-7xl px-4 py-10">
      <h1 className="mb-6 text-3xl font-black">Orders</h1>
      <div className="grid gap-3">
        {orders.length === 0 && <div className="rounded-lg bg-white p-6 dark:bg-white/5">No orders yet.</div>}
        {orders.map((order) => (
          <article key={order.orderNumber} className="rounded-lg bg-white p-5 dark:bg-white/5">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <strong>{order.orderNumber}</strong>
              <span className="rounded-full bg-signal/10 px-3 py-1 text-xs font-black text-signal">{order.status}</span>
              <span>Rs. {Number(order.totalAmount).toLocaleString('en-IN')}</span>
              <Link className="font-bold text-signal" to={`/orders/${order.orderNumber}`}>Track order</Link>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
