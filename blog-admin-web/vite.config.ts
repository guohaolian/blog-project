import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const isDev = mode === 'development'

  return {
    // In production the admin app is deployed under /admin/ (see deploy/nginx/blog.conf.example).
    // In development we want to work at / to avoid base-path refresh warnings.
    base: isDev ? '/' : '/admin/',
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
  }
})
