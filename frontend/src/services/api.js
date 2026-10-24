import axios from 'axios';
import { store } from '../redux/store.js';
import { logout, setCredentials } from '../redux/slices/authSlice.js';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
  timeout: 15_000
});

api.interceptors.request.use((config) => {
  const token = store.getState().auth.accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

function clearSession() {
  store.dispatch(logout());
  if (!window.location.pathname.startsWith('/login')) {
    window.location.assign('/login');
  }
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config ?? {};
    const refreshToken = store.getState().auth.refreshToken;
    const isRefreshRequest = original.url?.includes('/auth/refresh-token') || original.url?.includes('/auth/refresh');

    if (error.response?.status === 401 && refreshToken && !original._retry && !isRefreshRequest) {
      original._retry = true;
      try {
        const response = await api.post('/auth/refresh-token', { refreshToken });
        store.dispatch(setCredentials(response.data.data));
        original.headers = original.headers ?? {};
        original.headers.Authorization = `Bearer ${response.data.data.accessToken}`;
        return api(original);
      } catch (refreshError) {
        clearSession();
        return Promise.reject(refreshError);
      }
    }

    if (error.response?.status === 401 || (isRefreshRequest && error.response?.status >= 400)) {
      clearSession();
    }
    return Promise.reject(error);
  }
);
