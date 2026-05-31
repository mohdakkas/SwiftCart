import { Navigate, Outlet } from 'react-router-dom';
import { useSelector } from 'react-redux';

export function AdminRoute() {
  const { accessToken, role, user } = useSelector((state) => state.auth);
  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }
  if (role !== 'ROLE_ADMIN' && !user?.roles?.includes('ROLE_ADMIN')) {
    return <Navigate to="/unauthorized" replace />;
  }
  return <Outlet />;
}
