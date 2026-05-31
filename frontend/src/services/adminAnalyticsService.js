import { api } from './api.js';

export const adminAnalyticsService = {
  summary: async () => (await api.get('/admin/dashboard/summary')).data.data,
  topProducts: async () => (await api.get('/admin/dashboard/top-products')).data.data,
  recentOrders: async () => (await api.get('/admin/dashboard/recent-orders')).data.data
};
