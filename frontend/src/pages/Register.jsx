import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { Button } from '../components/Button.jsx';
import { authService } from '../services/authService.js';
import { setCredentials } from '../redux/slices/authSlice.js';

export function Register() {
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const onSubmit = async (values) => {
    const data = await authService.register(values);
    dispatch(setCredentials(data));
    toast.success('Account created');
    navigate('/', { replace: true });
  };

  return (
    <section className="mx-auto grid min-h-[70vh] max-w-lg content-center px-4">
      <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 rounded-lg bg-white p-6 shadow-soft dark:bg-white/5">
        <h1 className="text-3xl font-black">Create account</h1>
        <label className="grid gap-1 text-sm font-semibold">
          Full name
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="Full name" {...register('fullName', { required: 'Name is required' })} />
          {errors.fullName && <span className="text-xs text-red-600">{errors.fullName.message}</span>}
        </label>
        <label className="grid gap-1 text-sm font-semibold">
          Email
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="you@example.com" {...register('email', { required: 'Email is required', pattern: { value: /\S+@\S+\.\S+/, message: 'Enter a valid email' } })} />
          {errors.email && <span className="text-xs text-red-600">{errors.email.message}</span>}
        </label>
        <label className="grid gap-1 text-sm font-semibold">
          Password
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" type="password" placeholder="Password" {...register('password', { required: 'Password is required', minLength: { value: 8, message: 'Use at least 8 characters' } })} />
          {errors.password && <span className="text-xs text-red-600">{errors.password.message}</span>}
        </label>
        <label className="grid gap-1 text-sm font-semibold">
          Confirm password
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" type="password" placeholder="Confirm password" {...register('confirmPassword', { required: 'Confirm your password', validate: (value) => value === watch('password') || 'Passwords do not match' })} />
          {errors.confirmPassword && <span className="text-xs text-red-600">{errors.confirmPassword.message}</span>}
        </label>
        <Button disabled={isSubmitting}>Register</Button>
      </form>
    </section>
  );
}
