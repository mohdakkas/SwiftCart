export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#101418',
        mist: '#f6f7f9',
        signal: '#0f766e',
        coral: '#f9735b'
      },
      boxShadow: {
        soft: '0 18px 60px rgba(16, 20, 24, 0.10)'
      }
    }
  },
  plugins: []
};
