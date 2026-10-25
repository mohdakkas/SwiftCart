import { api } from './api.js';

export const catalogService = {
  products: async (params = {}) => {
    const res = await api.get('/products', { params });
    const payload = res.data?.data;
    // Extract content if Spring returned a Page object; otherwise fallback to array
    if (payload?.content && Array.isArray(payload.content)) {
      return payload.content;
    }
    return Array.isArray(payload) ? payload : [];
  },
  product: async (id) => (await api.get(`/products/${id}`)).data.data,
  categories: async () => (await api.get('/categories')).data.data
};