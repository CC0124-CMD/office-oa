import * as Sentry from '@sentry/vue'
import { BrowserTracing } from '@sentry/tracing'
import router from '@/router'

// 环境与配置解析（兼容 Vue CLI）
const NODE_ENV = process.env.NODE_ENV
const IS_PROD = NODE_ENV === 'production'
const IS_DEV = NODE_ENV === 'development'
const SENTRY_DSN = process.env.VUE_APP_SENTRY_DSN || ''
const APP_VERSION = process.env.VUE_APP_VERSION || '1.0.0'

// Sentry 配置
export function initSentry(app) {
  // 仅在生产环境且配置了 DSN 时启用
  if (IS_PROD && SENTRY_DSN) {
    Sentry.init({
      app,
      dsn: SENTRY_DSN,

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
      environment: NODE_ENV,

      // 发布版本
      release: APP_VERSION,

      // 错误过滤
      beforeSend(event, hint) {
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

      // 调试模式
      debug: IS_DEV,
    })
  }
}

// 手动捕获错误
export function captureError(error, context = {}) {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.captureException(error, {
      extra: context,
      tags: {
        component: context.component || 'unknown',
        action: context.action || 'unknown',
      },
    })
  } else {
    // 开发环境下打印错误
    // eslint-disable-next-line no-console
    console.error('Error captured:', error, context)
  }
}

// 手动捕获消息
export function captureMessage(message, level = 'info', context = {}) {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.captureMessage(message, {
      level,
      extra: context,
    })
  } else {
    // eslint-disable-next-line no-console
    console.log(`[${level.toUpperCase()}] ${message}`, context)
  }
}

// 设置用户信息
export function setUser(user) {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.setUser({
      id: user.username,
      username: user.username,
      email: user.email,
    })
  }
}

// 清除用户信息
export function clearUser() {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.setUser(null)
  }
}

// 添加面包屑
export function addBreadcrumb(breadcrumb) {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.addBreadcrumb(breadcrumb)
  }
}

// 设置标签
export function setTag(key, value) {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.setTag(key, value)
  }
}

// 设置上下文
export function setContext(name, context) {
  if (IS_PROD && SENTRY_DSN) {
    Sentry.setContext(name, context)
  }
} 