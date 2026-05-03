import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  plugins: [tailwindcss()],
  server: {
    port: 3000,
    strictPort: true
  },
  build: {
    rollupOptions: {
      input: {
        index: path.resolve(__dirname, 'index.html'),
        contacto: path.resolve(__dirname, 'contacto.html'),
        fondos: path.resolve(__dirname, 'fondos.html'),
        sobreNosotros: path.resolve(__dirname, 'sobre-nosotros.html'),
        tarifas: path.resolve(__dirname, 'tarifas.html'),
      }
    },
    outDir: path.resolve(__dirname, 'dist'),
    emptyOutDir: true
  }
})