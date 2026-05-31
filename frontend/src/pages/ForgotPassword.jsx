import { useForm } from 'react-hook-form';
import { Button } from '../components/Button.jsx';

export function ForgotPassword() {
  const { register, handleSubmit } = useForm();
  return (
    <section className="mx-auto grid min-h-[70vh] max-w-md content-center px-4">
      <form onSubmit={handleSubmit(() => {})} className="grid gap-4 rounded-lg bg-white p-6 shadow-soft dark:bg-white/5">
        <h1 className="text-3xl font-black">Forgot password</h1>
        <input className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Email" {...register('email')} />
        <Button>Send reset link</Button>
      </form>
    </section>
  );
}
