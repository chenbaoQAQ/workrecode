import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from './axios'

const app = createApp(App)

// 注册Element Plus组件库
app.use(ElementPlus)

// 注册路由
app.use(router)

// 全局挂载axios
app.config.globalProperties.$axios = axios

app.mount('#app')