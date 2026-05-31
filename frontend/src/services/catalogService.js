import { api } from './api.js';

export const catalogService = {
  products: async (params = {}) => (await api.get('/products', { params })).data.data,
  product: async (id) => (await api.get(`/products/${id}`)).data.data,
  categories: async () => (await api.get('/categories')).data.data
};
