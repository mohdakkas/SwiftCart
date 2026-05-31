import { Link } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';
import { Button } from '../components/Button.jsx';

export function Unauthorized() {
  return (
    <section className="mx-auto grid min-h-[70vh] max-w-xl content-center px-4 text-center">
      <div className="rounded-lg bg-white p-8 shadow-soft dark:bg-white/5">
        <ShieldAlert className="mx-auto mb-4 text-coral" size={44} />
        <h1 className="text-3xl font-black">Access Denied</h1>
        <p className="mt-3 text-black/60 dark:text-white/65">Your account does not have permission to access this area.</p>
        <Link to="/"><Button className="mt-6">Back to store</Button></Link>
      </div>
    </section>
  );
}
