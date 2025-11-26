import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: { port: 3000 },
  esbuild: {
    loader: 'jsx',
  },
  define: { global: 'window' },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/setupTests.js']
  }
})
