import { api } from './api.js';

export const productImageService = {
  uploadMain: async (productId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return (await api.post(`/admin/products/${productId}/image`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })).data.data;
  },
  uploadGallery: async (productId, files) => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    return (await api.post(`/admin/products/${productId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })).data.data;
  },
  remove: async (imageId) => (await api.delete(`/admin/products/images/${imageId}`)).data.data
};
