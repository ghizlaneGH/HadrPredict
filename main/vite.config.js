import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import viteTsconfigPaths from "vite-tsconfig-paths";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  // depending on your application, base can also be "/"
  base: "",
  plugins: [react(), viteTsconfigPaths(), tailwindcss()],
  build: {
    sourcemap: false, // ⛔ empêche la création des fichiers .map
  },
  server: {
    sourcemap: false,
    // this ensures that the browser opens upon server start
    open: true,
    // this sets a default port to 3000
    port: 3000,
    proxy: {
      "/api": {
        target: "http://localhost:8081",
        changeOrigin: true,
        secure: false,
        //  rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
});
