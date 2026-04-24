import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import axios from './axios'

const app = createApp(App)

// 注册Element Plus组件库，并使用中文日历/组件文案
app.use(ElementPlus, { locale: zhCn })

// 注册路由
app.use(router)

// 全局挂载axios
app.config.globalProperties.$axios = axios

app.mount('#app')
