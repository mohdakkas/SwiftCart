import { api } from './api.js';

export const paymentService = {
  simulate: async ({ method, amount, forceFailure = false }) => (
    await api.post('/payments/simulate', { method, amount, forceFailure })
  ).data.data,
  createRazorpayOrder: async (payload) => (await api.post('/payments/razorpay/order', payload)).data.data,
  verifyRazorpayPayment: async (payload) => (await api.post('/payments/razorpay/verify', payload)).data.data,
  markRazorpayFailed: async (payload) => (await api.post('/payments/razorpay/failure', payload)).data.data
};
