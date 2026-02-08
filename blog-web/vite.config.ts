import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    // keep warning but move it up a bit; real optimization is via code-splitting
    chunkSizeWarningLimit: 900,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return

          // get the top-level package name under node_modules
          const parts = id.split('node_modules/')[1]
          if (!parts) return
          const segs = parts.split('/')
          const pkg = segs[0]?.startsWith('@') ? `${segs[0]}/${segs[1]}` : segs[0]

          if (!pkg) return

          // group important libs
          if (pkg === 'vue' || pkg === '@vue' || pkg === 'vue-router' || pkg === 'pinia') return 'vendor-vue'
          if (pkg === 'element-plus' || pkg.startsWith('@element-plus')) return 'vendor-element-plus'

          // default: one chunk per package helps split without circular deps
          return `vendor-${pkg.replace('@', '').replace('/', '-')}`
        },
      },
    },
  },
  server: {
    port: 5173,
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
    port: 4173,
  },
})
