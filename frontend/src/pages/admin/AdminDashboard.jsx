import { useQuery } from '@tanstack/react-query';
import { Boxes, CheckCircle2, IndianRupee, Package, ShoppingCart, Users, XCircle } from 'lucide-react';
import { adminAnalyticsService } from '../../services/adminAnalyticsService.js';
import { StatCard } from '../../components/StatCard.jsx';

function SkeletonCard() {
  return <div className="h-28 animate-pulse rounded-lg border border-black/10 bg-white shadow-sm" />;
}

function ErrorPanel({ message }) {
  return <div className="rounded-lg bg-red-50 p-4 text-sm font-semibold text-red-700">{message}</div>;
}

function DashboardCard({ label, value, icon: Icon }) {
  return (
    <div className="rounded-lg border border-black/10 bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <div>
          <div className="text-sm font-semibold text-black/55">{label}</div>
          <div className="mt-2 text-3xl font-black">{value}</div>
        </div>
        <div className="rounded-md bg-signal/10 p-3 text-signal"><Icon size={22} /></div>
      </div>
    </div>
  );
}

export function AdminDashboard() {
  const summaryQuery = useQuery({ queryKey: ['admin-dashboard-summary'], queryFn: adminAnalyticsService.summary, retry: false });
  const topProductsQuery = useQuery({ queryKey: ['admin-top-products'], queryFn: adminAnalyticsService.topProducts, retry: false });
  const recentOrdersQuery = useQuery({ queryKey: ['admin-recent-orders'], queryFn: adminAnalyticsService.recentOrders, retry: false });
  const summary = summaryQuery.data;
  const topProducts = topProductsQuery.data ?? [];
  const recentOrders = recentOrdersQuery.data ?? [];

  return (
    <section className="p-6">
      <div>
        <h1 className="text-3xl font-black">Dashboard analytics</h1>
        <p className="mt-1 text-sm text-black/55">Live sales, order, product, and customer performance.</p>
      </div>

      {summaryQuery.isError && <div className="mt-6"><ErrorPanel message="Could not load dashboard summary. Make sure you are logged in as ADMIN." /></div>}

      <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {summaryQuery.isLoading && Array.from({ length: 4 }).map((_, index) => <SkeletonCard key={index} />)}
        {summary && (
          <>
            <DashboardCard label="Total sales" value={summary.totalSales} icon={ShoppingCart} />
            <DashboardCard label="Total revenue" value={`Rs. ${Number(summary.totalRevenue).toLocaleString('en-IN')}`} icon={IndianRupee} />
            <DashboardCard label="Total orders" value={summary.totalOrders} icon={Package} />
            <DashboardCard label="Total users" value={summary.totalUsers} icon={Users} />
          </>
        )}
      </div>

      {summary && (
        <div className="mt-4 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <StatCard label="Total products" value={summary.totalProducts} />
          <DashboardCard label="Pending orders" value={summary.pendingOrders} icon={Boxes} />
          <DashboardCard label="Delivered orders" value={summary.deliveredOrders} icon={CheckCircle2} />
          <DashboardCard label="Cancelled orders" value={summary.cancelledOrders} icon={XCircle} />
        </div>
      )}

      <div className="mt-6 grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
        <div className="overflow-hidden rounded-lg border border-black/10 bg-white shadow-sm">
          <div className="border-b border-black/10 p-5">
            <h2 className="text-xl font-black">Recent orders</h2>
          </div>
          {recentOrdersQuery.isError && <div className="p-5"><ErrorPanel message="Could not load recent orders." /></div>}
          {recentOrdersQuery.isLoading && <div className="m-5 h-52 animate-pulse rounded bg-black/5" />}
          {!recentOrdersQuery.isLoading && (
            <table className="w-full text-left text-sm">
              <thead className="bg-black/5">
                <tr>
                  <th className="p-3">Order</th>
                  <th className="p-3">Customer</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Payment</th>
                  <th className="p-3">Total</th>
                  <th className="p-3">Created</th>
                </tr>
              </thead>
              <tbody>
                {recentOrders.length === 0 && <tr><td className="p-5 text-black/55" colSpan="6">No recent orders.</td></tr>}
                {recentOrders.map((order) => (
                  <tr key={order.orderNumber} className="border-t border-black/10">
                    <td className="p-3 font-bold text-signal">{order.orderNumber}</td>
                    <td className="p-3">{order.customerName}</td>
                    <td className="p-3">{order.orderStatus}</td>
                    <td className="p-3">{order.paymentStatus}</td>
                    <td className="p-3">Rs. {Number(order.totalAmount).toLocaleString('en-IN')}</td>
                    <td className="p-3">{new Date(order.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="overflow-hidden rounded-lg border border-black/10 bg-white shadow-sm">
          <div className="border-b border-black/10 p-5">
            <h2 className="text-xl font-black">Top-selling products</h2>
          </div>
          {topProductsQuery.isError && <div className="p-5"><ErrorPanel message="Could not load top-selling products." /></div>}
          {topProductsQuery.isLoading && <div className="m-5 h-52 animate-pulse rounded bg-black/5" />}
          {!topProductsQuery.isLoading && (
            <div className="grid divide-y divide-black/10">
              {topProducts.length === 0 && <div className="p-5 text-sm text-black/55">No product sales yet.</div>}
              {topProducts.map((product) => (
                <div key={product.productId} className="grid grid-cols-[56px_1fr_auto] items-center gap-3 p-4">
                  <img
                    src={product.imageUrl || 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3'}
                    alt={product.productName}
                    className="h-14 w-14 rounded-md object-cover"
                  />
                  <div>
                    <div className="line-clamp-1 font-bold">{product.productName}</div>
                    <div className="text-xs text-black/55">{product.totalSold} sold</div>
                  </div>
                  <div className="text-right font-black">Rs. {Number(product.totalRevenue).toLocaleString('en-IN')}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
