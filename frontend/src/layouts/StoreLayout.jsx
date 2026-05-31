import { Link, Outlet, NavLink } from 'react-router-dom';
import { Heart, LogOut, Moon, Search, ShoppingCart, Sun, User } from 'lucide-react';
import { useDispatch, useSelector } from 'react-redux';
import { toggleTheme } from '../redux/slices/uiSlice.js';
import { logout } from '../redux/slices/authSlice.js';
import { authService } from '../services/authService.js';

export function StoreLayout() {
  const dispatch = useDispatch();
  const { darkMode } = useSelector((state) => state.ui);
  const { user, refreshToken } = useSelector((state) => state.auth);

  const handleLogout = async () => {
    try {
      if (refreshToken) await authService.logout(refreshToken);
    } finally {
      dispatch(logout());
      window.location.assign('/login');
    }
  };

  return (
    <div className="min-h-screen bg-mist text-ink transition dark:bg-ink dark:text-white">
      <header className="sticky top-0 z-30 border-b border-black/10 bg-white/90 backdrop-blur dark:border-white/10 dark:bg-ink/90">
        <div className="mx-auto flex max-w-7xl items-center gap-4 px-4 py-3">
          <Link to="/" className="text-2xl font-black tracking-tight">SwiftCart</Link>
          <nav className="hidden items-center gap-5 text-sm font-semibold md:flex">
            <NavLink to="/products">Products</NavLink>
            <NavLink to="/categories">Categories</NavLink>
            <NavLink to="/orders">Orders</NavLink>
          </nav>
          <div className="ml-auto hidden flex-1 max-w-md items-center rounded-full border border-black/10 bg-black/5 px-3 py-2 dark:border-white/10 dark:bg-white/10 md:flex">
            <Search size={18} />
            <input className="w-full bg-transparent px-2 text-sm outline-none" placeholder="Search products, brands, categories" />
          </div>
          <button aria-label="Toggle theme" onClick={() => dispatch(toggleTheme())} className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10">
            {darkMode ? <Sun size={20} /> : <Moon size={20} />}
          </button>
          <Link aria-label="Wishlist" to="/wishlist" className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10"><Heart size={20} /></Link>
          <Link aria-label="Cart" to="/cart" className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10"><ShoppingCart size={20} /></Link>
          {user ? (
            <>
              <Link aria-label="Profile" to="/profile" className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10"><User size={20} /></Link>
              <button aria-label="Logout" onClick={handleLogout} className="rounded-full p-2 hover:bg-black/5 dark:hover:bg-white/10"><LogOut size={20} /></button>
            </>
          ) : (
            <div className="flex items-center gap-2 text-sm font-bold">
              <Link to="/login" className="rounded-md px-3 py-2 hover:bg-black/5 dark:hover:bg-white/10">Login</Link>
              <Link to="/register" className="rounded-md bg-ink px-3 py-2 text-white dark:bg-white dark:text-ink">Register</Link>
            </div>
          )}
        </div>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
