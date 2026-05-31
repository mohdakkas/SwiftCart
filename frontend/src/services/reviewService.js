import { api } from './api.js';

export const reviewService = {
  list: async (productId) => (await api.get(`/products/${productId}/reviews`)).data.data,
  create: async (payload) => (await api.post('/reviews', payload)).data.data,
  update: async (reviewId, payload) => (await api.put(`/reviews/${reviewId}`, payload)).data.data,
  remove: async (reviewId) => (await api.delete(`/reviews/${reviewId}`)).data.data
};
