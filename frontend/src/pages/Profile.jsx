import { useSelector } from 'react-redux';
import { Button } from '../components/Button.jsx';

export function Profile() {
  const { user } = useSelector((state) => state.auth);
  return (
    <section className="mx-auto max-w-3xl px-4 py-10">
      <h1 className="mb-6 text-3xl font-black">Profile</h1>
      <div className="grid gap-4 rounded-lg bg-white p-6 shadow-sm dark:bg-white/5">
        <div className="grid gap-3 sm:grid-cols-2">
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" defaultValue={user?.firstName ?? ''} />
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" defaultValue={user?.lastName ?? ''} />
        </div>
        <input className="rounded-md border px-3 py-2 dark:bg-white/10" defaultValue={user?.email ?? ''} />
        <input type="file" className="rounded-md border px-3 py-2 dark:bg-white/10" />
        <Button>Update profile</Button>
      </div>
    </section>
  );
}
