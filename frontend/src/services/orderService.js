import { api } from './api.js';

export const orderService = {
  place: async (payload) => (await api.post('/orders', payload)).data.data,
  history: async () => (await api.get('/orders')).data.data,
  manage: async () => (await api.get('/orders/manage')).data.data,
  tracking: async (orderNumber) => (await api.get(`/orders/${orderNumber}/tracking`)).data.data,
  updateStatus: async (orderNumber, payload) => (await api.patch(`/orders/manage/${orderNumber}/status`, payload)).data.data
};
