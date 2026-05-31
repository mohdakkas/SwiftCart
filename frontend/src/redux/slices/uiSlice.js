import { createSlice } from '@reduxjs/toolkit';

const uiSlice = createSlice({
  name: 'ui',
  initialState: { darkMode: localStorage.getItem('swiftcart-theme') === 'dark' },
  reducers: {
    toggleTheme: (state) => {
      state.darkMode = !state.darkMode;
      document.documentElement.classList.toggle('dark', state.darkMode);
      localStorage.setItem('swiftcart-theme', state.darkMode ? 'dark' : 'light');
    }
  }
});

export const { toggleTheme } = uiSlice.actions;
export default uiSlice.reducer;
