import { createSlice } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',
  initialState: { items: [], subtotal: 0, tax: 0, shipping: 0, total: 0 },
  reducers: {
    setCart: (_state, action) => action.payload,
    clearCart: () => ({ items: [], subtotal: 0, tax: 0, shipping: 0, total: 0 })
  }
});

export const { setCart, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
