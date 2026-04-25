import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  plugins: [tailwindcss()],
  root: path.resolve(__dirname, 'public'),
  server: {
      port: 3000,
      strictPort: true
    },
  build: {
    rollupOptions: {
      input: {
        index: path.resolve(__dirname, 'public/index.html'),
        contacto: path.resolve(__dirname, 'public/contacto.html'),
        fondos: path.resolve(__dirname, 'public/fondos.html'),
        sobreNosotros: path.resolve(__dirname, 'public/sobre-nosotros.html'),
        tarifas: path.resolve(__dirname, 'public/tarifas.html'),
      }
    },
    outDir: path.resolve(__dirname, 'dist'),
    emptyOutDir: true
  },
  resolve: {
    alias: {
      '/js': path.resolve(__dirname, 'js'),
      '/styles': path.resolve(__dirname, 'styles'),
    }
  }
})