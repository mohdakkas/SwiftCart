import { Navigate, Outlet } from 'react-router-dom';
import { useSelector } from 'react-redux';

export function ProtectedRoute({ roles = [] }) {
  const { user, accessToken, role } = useSelector((state) => state.auth);
  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }
  if (roles.length > 0 && !roles.some((requiredRole) => role === requiredRole || user?.roles?.includes(requiredRole))) {
    return <Navigate to="/unauthorized" replace />;
  }
  return <Outlet />;
}
