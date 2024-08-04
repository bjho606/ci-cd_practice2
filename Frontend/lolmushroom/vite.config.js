import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://http://127.0.0.1:5000',
        // target: 'https://i11a401.p.ssafy.io',
        changeOrigin: true
      }
    }
  },
  define: {
    global: {}
  }
})
