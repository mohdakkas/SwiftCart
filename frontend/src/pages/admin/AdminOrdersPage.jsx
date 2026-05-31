import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-toastify';
import { orderService } from '../../services/orderService.js';

const statuses = ['PLACED', 'CONFIRMED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED', 'RETURNED', 'REFUNDED'];

export function AdminOrdersPage() {
  const queryClient = useQueryClient();
  const [messages, setMessages] = useState({});
  const { data } = useQuery({ queryKey: ['admin-orders'], queryFn: orderService.manage, retry: false });
  const orders = data?.content ?? [];

  const updateStatus = useMutation({
    mutationFn: ({ orderNumber, status }) => orderService.updateStatus(orderNumber, { status, message: messages[orderNumber] }),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['admin-orders'] });
      queryClient.invalidateQueries({ queryKey: ['order-tracking', variables.orderNumber] });
      toast.success('Order status updated');
    },
    onError: (error) => toast.error(error.response?.data?.message ?? 'Invalid status update')
  });

  return (
    <section className="p-6">
      <h1 className="text-3xl font-black">Order management</h1>
      <p className="mt-1 text-sm text-black/55">Update order status and keep a complete customer-visible tracking history.</p>
      <div className="mt-6 overflow-hidden rounded-lg border bg-white shadow-sm">
        <table className="w-full text-left text-sm">
          <thead className="bg-black/5">
            <tr>
              <th className="p-3">Order</th>
              <th className="p-3">Status</th>
              <th className="p-3">Total</th>
              <th className="p-3">Tracking message</th>
              <th className="p-3">Update</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 && <tr><td className="p-5 text-black/55" colSpan="5">No orders found.</td></tr>}
            {orders.map((order) => (
              <tr key={order.orderNumber} className="border-t align-top">
                <td className="p-3 font-bold"><Link className="text-signal" to={`/orders/${order.orderNumber}`}>{order.orderNumber}</Link></td>
                <td className="p-3">{order.status}</td>
                <td className="p-3">Rs. {Number(order.totalAmount).toLocaleString('en-IN')}</td>
                <td className="p-3">
                  <input
                    className="w-full rounded-md border px-3 py-2"
                    placeholder="Optional status message"
                    value={messages[order.orderNumber] ?? ''}
                    onChange={(event) => setMessages((current) => ({ ...current, [order.orderNumber]: event.target.value }))}
                  />
                </td>
                <td className="p-3">
                  <select
                    className="rounded-md border px-3 py-2"
                    value={order.status}
                    onChange={(event) => updateStatus.mutate({ orderNumber: order.orderNumber, status: event.target.value })}
                    disabled={updateStatus.isPending}
                  >
                    {statuses.map((status) => <option key={status} value={status}>{status}</option>)}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
