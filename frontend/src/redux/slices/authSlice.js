import { createSlice } from '@reduxjs/toolkit';

function loadSavedAuth() {
  try {
    return JSON.parse(localStorage.getItem('swiftcart-auth') ?? 'null');
  } catch {
    localStorage.removeItem('swiftcart-auth');
    localStorage.removeItem('swiftcart-token');
    localStorage.removeItem('swiftcart-role');
    return null;
  }
}

const saved = loadSavedAuth();

const authSlice = createSlice({
  name: 'auth',
  initialState: saved ?? { accessToken: null, refreshToken: null, role: null, user: null },
  reducers: {
    setCredentials: (state, action) => {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.role = action.payload.role ?? action.payload.user?.role ?? action.payload.user?.roles?.[0] ?? null;
      state.user = action.payload.user;
      localStorage.setItem('swiftcart-auth', JSON.stringify(state));
      if (state.accessToken) localStorage.setItem('swiftcart-token', state.accessToken);
      if (state.role) localStorage.setItem('swiftcart-role', state.role);
    },
    logout: (state) => {
      state.accessToken = null;
      state.refreshToken = null;
      state.role = null;
      state.user = null;
      localStorage.removeItem('swiftcart-auth');
      localStorage.removeItem('swiftcart-token');
      localStorage.removeItem('swiftcart-role');
    }
  }
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
