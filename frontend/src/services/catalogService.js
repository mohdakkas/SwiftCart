import { api } from './api.js';

export const catalogService = {
  products: async (params = {}) => {
    const res = await api.get('/products', { params });
    const payload = res.data?.data;

    // If Spring Boot returns a Page object, the array is inside payload.content
    if (payload?.content && Array.isArray(payload.content)) {
      return payload.content;
    }
    // If it's already an array
    if (Array.isArray(payload)) {
      return payload;
    }
    return [];
  },

  product: async (id) => {
    const res = await api.get(`/products/${id}`);
    return res.data?.data;
  },

  categories: async () => {
    const res = await api.get('/categories');
    return res.data?.data;
  }
};