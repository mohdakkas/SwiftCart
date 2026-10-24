import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Eye, EyeOff } from 'lucide-react';
import { toast } from 'react-toastify';
import { Button } from '../components/Button.jsx';
import { authService } from '../services/authService.js';
import { setCredentials } from '../redux/slices/authSlice.js';

export function Login() {
  const [showPassword, setShowPassword] = useState(false);
  const [serverError, setServerError] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    defaultValues: { email: 'maya@swiftcart.com', password: 'User@12345' }
  });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const onSubmit = async (values) => {
    setServerError('');
    try {
      const data = await authService.login(values);
      dispatch(setCredentials(data));
      toast.success('Welcome back');
      const redirectTo = data.role === 'ROLE_ADMIN' ? '/admin' : location.state?.from ?? '/';
      navigate(redirectTo, { replace: true });
    } catch (error) {
      setServerError(error.response?.data?.message ?? 'Invalid email or password');
    }
  };

  return (
    <section className="mx-auto grid min-h-[70vh] max-w-md content-center px-4">
      <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 rounded-lg bg-white p-6 shadow-soft dark:bg-white/5">
        <h1 className="text-3xl font-black">Login</h1>
        {serverError && <div className="rounded-md bg-red-50 px-3 py-2 text-sm font-semibold text-red-700">{serverError}</div>}
        <label className="grid gap-1 text-sm font-semibold">
          Email
          <input className="rounded-md border px-3 py-2 dark:bg-white/10" placeholder="you@example.com" {...register('email', { required: 'Email is required', pattern: { value: /\S+@\S+\.\S+/, message: 'Enter a valid email' } })} />
          {errors.email && <span className="text-xs text-red-600">{errors.email.message}</span>}
        </label>
        <label className="grid gap-1 text-sm font-semibold">
          Password
          <div className="flex rounded-md border dark:bg-white/10">
            <input className="min-w-0 flex-1 bg-transparent px-3 py-2 outline-none" type={showPassword ? 'text' : 'password'} placeholder="Password" {...register('password', { required: 'Password is required' })} />
            <button type="button" aria-label="Show password" onClick={() => setShowPassword((value) => !value)} className="px-3">
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {errors.password && <span className="text-xs text-red-600">{errors.password.message}</span>}
        </label>
        <Button disabled={isSubmitting}>Login</Button>
        <div className="flex justify-between text-sm"><Link to="/forgot-password">Forgot password?</Link><Link to="/register">Create account</Link></div>
      </form>
    </section>
  );
}
