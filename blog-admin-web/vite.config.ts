import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  // Admin app is deployed under /admin/ (see deploy/nginx/blog.conf.example)
  // Without this, built asset URLs may point to /assets/... and fail in prod.
  base: '/admin/',
  plugins: [vue()],
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
  },
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  preview: {
    // npm run preview will serve with the same base (/admin/)
    port: 4174,
  },
})
