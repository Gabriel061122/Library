import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/users': 'http://localhost:8080',
      '/books': 'http://localhost:8080',
      '/orders': 'http://localhost:8080',
      '/buys': 'http://localhost:8080',
      '/borrowings': 'http://localhost:8080',
      '/borrowing-copies': 'http://localhost:8080',
      '/user-types': 'http://localhost:8080',
    },
  },
})
