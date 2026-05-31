import { Route, Routes } from 'react-router-dom';
import { StoreLayout } from '../layouts/StoreLayout.jsx';
import { AdminLayout } from '../layouts/AdminLayout.jsx';
import { ProtectedRoute } from './ProtectedRoute.jsx';
import { AdminRoute } from './AdminRoute.jsx';
import { Home } from '../pages/Home.jsx';
import { ProductListing } from '../pages/ProductListing.jsx';
import { ProductDetails } from '../pages/ProductDetails.jsx';
import { Categories } from '../pages/Categories.jsx';
import { Login } from '../pages/Login.jsx';
import { Register } from '../pages/Register.jsx';
import { ForgotPassword } from '../pages/ForgotPassword.jsx';
import { Cart } from '../pages/Cart.jsx';
import { Checkout } from '../pages/Checkout.jsx';
import { Orders } from '../pages/Orders.jsx';
import { OrderDetails } from '../pages/OrderDetails.jsx';
import { Profile } from '../pages/Profile.jsx';
import { Addresses } from '../pages/Addresses.jsx';
import { Wishlist } from '../pages/Wishlist.jsx';
import { UserDashboard } from '../pages/UserDashboard.jsx';
import { AdminDashboard } from '../pages/admin/AdminDashboard.jsx';
import { AdminOrdersPage } from '../pages/admin/AdminOrdersPage.jsx';
import { AdminProductsPage } from '../pages/admin/AdminProductsPage.jsx';
import { AdminResourcePage } from '../pages/admin/AdminResourcePage.jsx';
import { Unauthorized } from '../pages/Unauthorized.jsx';

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<StoreLayout />}>
        <Route index element={<Home />} />
        <Route path="products" element={<ProductListing />} />
        <Route path="products/:id" element={<ProductDetails />} />
        <Route path="categories" element={<Categories />} />
        <Route path="search" element={<ProductListing />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="forgot-password" element={<ForgotPassword />} />
        <Route path="unauthorized" element={<Unauthorized />} />
        <Route element={<ProtectedRoute />}>
          <Route path="dashboard" element={<UserDashboard />} />
          <Route path="wishlist" element={<Wishlist />} />
          <Route path="cart" element={<Cart />} />
          <Route path="checkout" element={<Checkout />} />
          <Route path="orders" element={<Orders />} />
          <Route path="orders/:orderNumber" element={<OrderDetails />} />
          <Route path="profile" element={<Profile />} />
          <Route path="addresses" element={<Addresses />} />
        </Route>
      </Route>
      <Route element={<AdminRoute />}>
        <Route path="admin" element={<AdminLayout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="products" element={<AdminProductsPage />} />
          <Route path="orders" element={<AdminOrdersPage />} />
          {['categories', 'users', 'payments', 'reports'].map((resource) => (
            <Route key={resource} path={resource} element={<AdminResourcePage resource={resource} />} />
          ))}
        </Route>
      </Route>
    </Routes>
  );
}
