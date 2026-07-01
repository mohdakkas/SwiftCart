import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { orderService } from '../services/orderService.js';
import { OrderTrackingTimeline } from '../components/OrderTrackingTimeline.jsx';

export function OrderDetails() {
  const { orderNumber } = useParams();
  const { data: timeline, isLoading, error } = useQuery({
    queryKey: ['order-tracking', orderNumber],
    queryFn: () => orderService.tracking(orderNumber),
    retry: false
  });
  const address = timeline?.shippingAddress;

  return (
    <section className="mx-auto max-w-5xl px-4 py-10">
      <Link to="/orders" className="text-sm font-bold text-signal">Back to orders</Link>
      <h1 className="mt-3 text-3xl font-black">Order {orderNumber}</h1>
      {address && (
        <div className="mt-6 rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
          <h2 className="text-xl font-black">Shipping address</h2>
          <p className="mt-2 text-sm text-black/65 dark:text-white/70">{address.fullName} | {address.phoneNumber}</p>
          <p className="mt-1 text-sm text-black/65 dark:text-white/70">{address.addressLine1}{address.addressLine2 ? `, ${address.addressLine2}` : ''}</p>
          <p className="text-sm text-black/65 dark:text-white/70">{address.city}, {address.state} {address.postalCode}, {address.country}</p>
        </div>
      )}
      {isLoading && <div className="mt-6 rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">Loading tracking...</div>}
      {error && <div className="mt-6 rounded-lg bg-red-50 p-5 font-semibold text-red-700">Could not load order tracking.</div>}
      {timeline && <div className="mt-6"><OrderTrackingTimeline timeline={timeline} /></div>}
    </section>
  );
}
