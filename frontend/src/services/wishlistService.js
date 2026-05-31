import { api } from './api.js';

export const wishlistService = {
  get: async () => (await api.get('/wishlist')).data.data,
  add: async (productId) => (await api.post('/wishlist/items', { productId })).data.data,
  remove: async (itemId) => (await api.delete(`/wishlist/items/${itemId}`)).data.data,
  moveToCart: async (itemId) => (await api.post(`/wishlist/items/${itemId}/move-to-cart`)).data.data
};
