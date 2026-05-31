import { NavLink, Outlet } from 'react-router-dom';

const links = ['dashboard', 'products', 'categories', 'orders', 'users', 'payments', 'reports'];

export function AdminLayout() {
  return (
    <div className="min-h-screen bg-[#f7f8fb] text-ink">
      <aside className="fixed inset-y-0 left-0 hidden w-64 border-r bg-white p-5 lg:block">
        <div className="mb-8 text-2xl font-black">SwiftCart Admin</div>
        <nav className="grid gap-1">
          {links.map((link) => (
            <NavLink key={link} to={`/admin/${link}`} className="rounded px-3 py-2 text-sm font-semibold capitalize hover:bg-black/5">
              {link}
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="lg:pl-64">
        <Outlet />
      </main>
    </div>
  );
}
