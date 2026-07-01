import { api } from './api.js';

export const addressService = {
  list: async () => (await api.get('/addresses')).data.data,
  get: async (id) => (await api.get(`/addresses/${id}`)).data.data,
  create: async (payload) => (await api.post('/addresses', payload)).data.data,
  update: async (id, payload) => (await api.put(`/addresses/${id}`, payload)).data.data,
  remove: async (id) => (await api.delete(`/addresses/${id}`)).data.data,
  setDefault: async (id) => (await api.put(`/addresses/${id}/default`)).data.data
};
