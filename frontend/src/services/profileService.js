import { api } from './api.js';

export const profileService = {
  get: async () => (await api.get('/profile')).data.data,
  addresses: async () => (await api.get('/addresses')).data.data,
  update: async (payload) => (await api.put('/profile', payload)).data.data,
  changePassword: async (payload) => (await api.patch('/profile/change-password', payload)).data,
  uploadImage: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return (await api.post('/profile/image', formData, { headers: { 'Content-Type': 'multipart/form-data' } })).data.data;
  }
};


