import { api } from './api.js';

export const authService = {
  login: async (payload) => (await api.post('/auth/login', payload)).data.data,
  register: async (payload) => (await api.post('/auth/register', payload)).data.data,
  logout: async (refreshToken) => (await api.post('/auth/logout', { refreshToken })).data,
  forgotPassword: async (payload) => (await api.post('/auth/forgot-password', payload)).data
};
