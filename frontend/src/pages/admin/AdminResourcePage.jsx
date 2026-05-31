import { Button } from '../../components/Button.jsx';

export function AdminResourcePage({ resource }) {
  return (
    <section className="p-6">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-black capitalize">{resource}</h1>
          <p className="mt-1 text-sm text-black/55">Manage {resource}, filters, bulk actions, and audit-friendly operations.</p>
        </div>
        <Button>Create</Button>
      </div>
      <div className="overflow-hidden rounded-lg border bg-white shadow-sm">
        <table className="w-full text-left text-sm">
          <thead className="bg-black/5"><tr><th className="p-3">Name</th><th className="p-3">Status</th><th className="p-3">Updated</th><th className="p-3">Actions</th></tr></thead>
          <tbody>
            {Array.from({ length: 6 }).map((_, index) => (
              <tr key={index} className="border-t">
                <td className="p-3 font-semibold capitalize">{resource} item {index + 1}</td>
                <td className="p-3">Active</td>
                <td className="p-3">Today</td>
                <td className="p-3"><button className="font-bold text-signal">Edit</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
