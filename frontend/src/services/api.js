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

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const refreshToken = store.getState().auth.refreshToken;
    if (error.response?.status === 401 && refreshToken && !original._retry) {
      original._retry = true;
      const response = await api.post('/auth/refresh-token', { refreshToken });
      store.dispatch(setCredentials(response.data.data));
      original.headers.Authorization = `Bearer ${response.data.data.accessToken}`;
      return api(original);
    }
    if (error.response?.status === 401) {
      store.dispatch(logout());
    }
    return Promise.reject(error);
  }
);
