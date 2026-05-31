import { CheckCircle2, Circle, PackageCheck } from 'lucide-react';

const deliverySteps = ['PLACED', 'CONFIRMED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED'];
const exceptionStatuses = ['CANCELLED', 'RETURNED', 'REFUNDED'];

const labels = {
  PLACED: 'Placed',
  CONFIRMED: 'Confirmed',
  SHIPPED: 'Shipped',
  OUT_FOR_DELIVERY: 'Out for delivery',
  DELIVERED: 'Delivered',
  CANCELLED: 'Cancelled',
  RETURNED: 'Returned',
  REFUNDED: 'Refunded'
};

export function OrderTrackingTimeline({ timeline }) {
  const history = timeline?.history ?? [];
  const currentStatus = timeline?.currentStatus;
  const terminalException = exceptionStatuses.includes(currentStatus);
  const currentIndex = deliverySteps.indexOf(currentStatus);
  const visibleSteps = terminalException ? [...deliverySteps.slice(0, Math.max(currentIndex + 1, 1)), currentStatus] : deliverySteps;
  const historyByStatus = history.reduce((acc, item) => ({ ...acc, [item.status]: item }), {});

  return (
    <div className="rounded-lg bg-white p-5 shadow-sm dark:bg-white/5">
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div>
          <div className="text-sm font-bold uppercase text-signal">Tracking timeline</div>
          <h2 className="mt-1 text-2xl font-black">{labels[currentStatus] ?? currentStatus}</h2>
        </div>
        <PackageCheck className="text-signal" size={34} />
      </div>
      <ol className="grid gap-4">
        {visibleSteps.map((status, index) => {
          const event = historyByStatus[status];
          const completed = terminalException ? Boolean(event) : index <= currentIndex;
          const current = status === currentStatus;
          return (
            <li key={status} className="grid grid-cols-[32px_1fr] gap-3">
              <div className="pt-1">
                {completed || current ? <CheckCircle2 className="fill-signal text-white" size={24} /> : <Circle className="text-black/25 dark:text-white/30" size={24} />}
              </div>
              <div className={`rounded-md border p-3 ${current ? 'border-signal bg-signal/5' : 'border-black/10 dark:border-white/10'}`}>
                <div className="font-black">{labels[status] ?? status}</div>
                <div className="mt-1 text-sm text-black/60 dark:text-white/65">
                  {event?.message ?? (completed ? 'Completed' : 'Waiting for update')}
                </div>
                {event?.updatedAt && <div className="mt-2 text-xs font-semibold text-black/45 dark:text-white/45">{new Date(event.updatedAt).toLocaleString()}</div>}
              </div>
            </li>
          );
        })}
      </ol>
    </div>
  );
}
