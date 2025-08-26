import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
// Defer Element Plus to reduce initial bundle
// import ElementPlus from 'element-plus'
// import 'element-plus/dist/index.css'
// import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import { initSentry } from './utils/sentry'

// 配置axios
axios.defaults.baseURL = 'http://localhost:8080/api'
axios.defaults.timeout = 10000

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 未授权，跳转到登录页
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

const app = createApp(App)

// 初始化 Sentry 错误监控
initSentry(app)

app.use(store)
app.use(router)
app.use(Antd)

app.mount('#app')

// 延迟加载 Element Plus 及其图标，避免首屏体积过大
if (process.env.NODE_ENV === 'production') {
  setTimeout(async () => {
    try {
      const [{ default: ElementPlus }, _icons] = await Promise.all([
        import('element-plus'),
        import('@element-plus/icons-vue')
      ])
      const icons = _icons
      app.use(ElementPlus)
      for (const [key, component] of Object.entries(icons)) {
        app.component(key, component)
      }
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log('Element Plus lazy load skipped:', e)
    }
  }, 0)
} else {
  // 开发环境仍然同步加载，提升开发体验
  ;(async () => {
    const [{ default: ElementPlus }, _icons] = await Promise.all([
      import('element-plus'),
      import('@element-plus/icons-vue')
    ])
    const icons = _icons
    app.use(ElementPlus)
    for (const [key, component] of Object.entries(icons)) {
      app.component(key, component)
    }
  })()
} 