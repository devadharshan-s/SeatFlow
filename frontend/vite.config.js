import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        host: true,
        proxy: {
            '/movie/': {
                target: 'http://127.0.0.1:8088',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/movie/, ''); },
                configure: function (proxy) {
                    proxy.on('proxyReq', function (proxyReq, req) {
                        console.log('[Proxy Request] MOVIE:', req.method, req.url, '->', proxyReq.path);
                    });
                    proxy.on('proxyRes', function (proxyRes, req) {
                        console.log('[Proxy Response] MOVIE:', proxyRes.statusCode, req.url);
                    });
                }
            },
            '/show-seat/': {
                target: 'http://127.0.0.1:8086',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/show-seat/, ''); },
                configure: function (proxy) {
                    proxy.on('proxyReq', function (proxyReq, req) {
                        console.log('[Proxy Request] SHOW-SEAT:', req.method, req.url, '->', proxyReq.path);
                    });
                    proxy.on('proxyRes', function (proxyRes, req) {
                        console.log('[Proxy Response] SHOW-SEAT:', proxyRes.statusCode, req.url);
                    });
                }
            },
            '/show/': {
                target: 'http://127.0.0.1:8086',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/show/, ''); },
                configure: function (proxy) {
                    proxy.on('proxyReq', function (proxyReq, req) {
                        console.log('[Proxy Request] SHOW:', req.method, req.url, '->', proxyReq.path);
                    });
                    proxy.on('proxyRes', function (proxyRes, req) {
                        console.log('[Proxy Response] SHOW:', proxyRes.statusCode, req.url);
                    });
                }
            },
            '/bookmyshow-booking-service/': {
                target: 'http://127.0.0.1:8085',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/bookmyshow-booking-service/, ''); },
                configure: function (proxy) {
                    proxy.on('proxyReq', function (proxyReq, req) {
                        console.log('[Proxy Request] BOOKING:', req.method, req.url, '->', proxyReq.path);
                    });
                    proxy.on('proxyRes', function (proxyRes, req) {
                        console.log('[Proxy Response] BOOKING:', proxyRes.statusCode, req.url);
                    });
                }
            },
            '/payment/': {
                target: 'http://127.0.0.1:8084',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/payment/, ''); },
                configure: function (proxy) {
                    proxy.on('proxyReq', function (proxyReq, req) {
                        console.log('[Proxy Request] PAYMENT:', req.method, req.url, '->', proxyReq.path);
                    });
                    proxy.on('proxyRes', function (proxyRes, req) {
                        console.log('[Proxy Response] PAYMENT:', proxyRes.statusCode, req.url);
                    });
                }
            },
            '/theatre/': {
                target: 'http://127.0.0.1:8087',
                changeOrigin: true,
                rewrite: function (path) { return path.replace(/^\/theatre/, ''); },
                configure: function (proxy) {
                    proxy.on('proxyReq', function (proxyReq, req) {
                        console.log('[Proxy Request] THEATRE:', req.method, req.url, '->', proxyReq.path);
                    });
                    proxy.on('proxyRes', function (proxyRes, req) {
                        console.log('[Proxy Response] THEATRE:', proxyRes.statusCode, req.url);
                    });
                }
            },
            '/login': {
                target: 'http://127.0.0.1:8089',
                changeOrigin: true,
            },
            '/createUser': {
                target: 'http://127.0.0.1:8089',
                changeOrigin: true,
            },
        },
    },
});
