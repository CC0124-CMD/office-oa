import * as Sentry from '@sentry/vue'
import { BrowserTracing } from '@sentry/tracing'
import router from '@/router'

// Sentry 配置
export function initSentry(app) {
  // 只在生产环境启用 Sentry
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.init({
      app,
      dsn: import.meta.env.VITE_SENTRY_DSN || process.env.VUE_APP_SENTRY_DSN,
      
      // 性能监控
      integrations: [
        new BrowserTracing({
          routingInstrumentation: Sentry.vueRouterInstrumentation(router),
          tracingOrigins: ['localhost', 'your-domain.vercel.app', /^\//],
        }),
      ],
      
      // 采样率：100% 捕获错误，10% 捕获性能数据
      tracesSampleRate: 0.1,
      
      // 环境标识
      environment: import.meta.env.MODE || process.env.NODE_ENV,
      
      // 发布版本
      release: import.meta.env.VITE_APP_VERSION || '1.0.0',
      
      // 错误过滤
      beforeSend(event, hint) {
        // 过滤掉一些常见的无害错误
        const error = hint.originalException
        
        // 忽略网络错误
        if (error && error.message && error.message.includes('Network Error')) {
          return null
        }
        
        // 忽略取消的请求
        if (error && error.name === 'CanceledError') {
          return null
        }
        
        // 忽略用户手动取消的操作
        if (error && error.message && error.message.includes('User cancelled')) {
          return null
        }
        
        return event
      },
      
      // 用户上下文
      beforeBreadcrumb(breadcrumb) {
        // 过滤掉敏感信息
        if (breadcrumb.category === 'navigation') {
          // 移除 URL 中的敏感参数
          if (breadcrumb.data && breadcrumb.data.url) {
            const url = new URL(breadcrumb.data.url)
            url.searchParams.delete('token')
            url.searchParams.delete('password')
            breadcrumb.data.url = url.toString()
          }
        }
        
        return breadcrumb
      },
      
      // 调试模式
      debug: import.meta.env.DEV || process.env.NODE_ENV === 'development',
    })
  }
}

// 手动捕获错误
export function captureError(error, context = {}) {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.captureException(error, {
      extra: context,
      tags: {
        component: context.component || 'unknown',
        action: context.action || 'unknown',
      },
    })
  } else {
    // 开发环境下打印错误
    console.error('Error captured:', error, context)
  }
}

// 手动捕获消息
export function captureMessage(message, level = 'info', context = {}) {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.captureMessage(message, {
      level,
      extra: context,
    })
  } else {
    console.log(`[${level.toUpperCase()}] ${message}`, context)
  }
}

// 设置用户信息
export function setUser(user) {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.setUser({
      id: user.username,
      username: user.username,
      email: user.email,
    })
  }
}

// 清除用户信息
export function clearUser() {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.setUser(null)
  }
}

// 添加面包屑
export function addBreadcrumb(breadcrumb) {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.addBreadcrumb(breadcrumb)
  }
}

// 设置标签
export function setTag(key, value) {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.setTag(key, value)
  }
}

// 设置上下文
export function setContext(name, context) {
  if (import.meta.env.PROD || process.env.NODE_ENV === 'production') {
    Sentry.setContext(name, context)
  }
} 