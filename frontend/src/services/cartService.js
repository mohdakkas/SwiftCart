import { api } from './api.js';

export const cartService = {
  get: async () => (await api.get('/cart')).data.data,
  add: async (payload) => (await api.post('/cart/items', payload)).data.data,
  update: async (itemId, payload) => (await api.patch(`/cart/items/${itemId}`, payload)).data.data,
  remove: async (itemId) => (await api.delete(`/cart/items/${itemId}`)).data.data
};
