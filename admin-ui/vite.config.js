import {resolve} from 'path'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
// https://github.com/vuejs/jsx-next#syntax
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig((env)=>{
  return {
    plugins: [
      vue(),
      vueJsx(),
    ],
    resolve: {
      alias: {
        '@': resolve(__dirname, './src'),
        '@msn': resolve(__dirname, './src/views')
      }
    },
    server: {
      proxy: {
        '^/api/*': {
          changeOrigin: true,
          // 线上测试地址, 只支持浏览数据
          // target: 'http://iot.iteaj.com/api',
          // 本地开发地址, 需要运行对应的java后端[https://gitee.com/iteaj/iboot]
          target: 'http://localhost:8085',
          rewrite: (path) => {
            return path.replace(/^\/api/, '')
          }
        },
      },
      cors: true,
      host: '0.0.0.0'
    },
    optimizeDeps: {
      include: [],
    },
    build: {
      rollupOptions: {
        input: {
          main: resolve(__dirname, "index.html"),
          le5le_plugin: resolve(__dirname, "src/plugin/le5le/le5le_plugin.js"),
          common_plugin: resolve(__dirname, "src/plugin/common/common_plugin.js")
        },
        // external: ['ant-design-vue'],
        output: {
          entryFileNames: (msn) => {
            if(msn.name.indexOf("_plugin") > 0) {
              let name = msn.name.replace("_plugin", "");
              return `assets/plugin/${name}.js`
            } else {
              return "assets/[name]-[hash].js"
            }
          }
        }
      },
      sourcemap: false,
      cssCodeSplit: true
    }
  }
})
