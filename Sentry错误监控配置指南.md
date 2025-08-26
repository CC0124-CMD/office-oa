# Sentry 错误监控配置指南

## 功能概述

Sentry 是一个强大的错误监控和性能追踪平台，可以帮助你：
- 实时捕获和报告 JavaScript 错误
- 追踪用户操作路径（面包屑）
- 监控应用性能
- 提供详细的错误上下文信息

## 配置步骤

### 1. 注册 Sentry 账号

1. 访问 [sentry.io](https://sentry.io)
2. 注册免费账号
3. 创建新项目，选择 "Vue.js" 框架

### 2. 获取 DSN

1. 在项目设置中找到 "Client Keys (DSN)"
2. 复制 DSN 地址，格式类似：
   ```
   https://your-sentry-dsn@sentry.io/project-id
   ```

### 3. 配置环境变量

在 Vercel 项目设置中添加环境变量：
- Name: `VITE_SENTRY_DSN`
- Value: 你的 Sentry DSN 地址

### 4. 部署测试

推送代码到 GitHub，Vercel 会自动重新部署。

## 功能特性

### ✅ 自动错误捕获

- **JavaScript 错误**: 自动捕获所有未处理的 JavaScript 错误
- **Vue 组件错误**: 捕获 Vue 组件中的错误
- **路由错误**: 捕获路由导航错误
- **网络错误**: 捕获 API 请求失败

### ✅ 性能监控

- **页面加载时间**: 监控页面加载性能
- **路由切换**: 追踪路由切换耗时
- **用户交互**: 监控用户操作响应时间

### ✅ 用户上下文

- **用户信息**: 自动关联错误与用户
- **操作路径**: 记录用户操作面包屑
- **环境信息**: 区分开发/生产环境

### ✅ 错误过滤

- **网络错误**: 自动过滤网络连接错误
- **用户取消**: 忽略用户主动取消的操作
- **敏感信息**: 自动移除 URL 中的敏感参数

## 使用方法

### 手动捕获错误

```javascript
import { captureError } from '@/utils/sentry'

try {
  // 你的代码
} catch (error) {
  captureError(error, {
    component: 'ComponentName',
    action: 'actionName',
    context: '错误上下文'
  })
}
```

### 手动捕获消息

```javascript
import { captureMessage } from '@/utils/sentry'

captureMessage('重要操作完成', 'info', {
  userId: 'user123',
  action: 'dataExport'
})
```

### 设置用户信息

```javascript
import { setUser } from '@/utils/sentry'

setUser({
  username: 'john_doe',
  email: 'john@example.com'
})
```

### 添加面包屑

```javascript
import { addBreadcrumb } from '@/utils/sentry'

addBreadcrumb({
  category: 'ui',
  message: '用户点击了按钮',
  level: 'info',
  data: {
    buttonId: 'submit-btn',
    page: 'login'
  }
})
```

## 监控面板

### 错误列表

在 Sentry 控制台可以看到：
- 错误发生时间
- 错误类型和消息
- 影响用户数量
- 错误频率趋势

### 错误详情

点击错误可以看到：
- 完整的错误堆栈
- 用户操作路径
- 浏览器环境信息
- 相关标签和上下文

### 性能监控

- 页面加载时间分布
- 路由切换性能
- 用户交互响应时间

## 最佳实践

### 1. 错误分类

使用标签对错误进行分类：
```javascript
setTag('feature', 'user-management')
setTag('severity', 'high')
```

### 2. 上下文信息

提供足够的上下文信息：
```javascript
setContext('user', {
  id: user.id,
  role: user.role,
  lastAction: user.lastAction
})
```

### 3. 错误过滤

避免捕获无害错误：
```javascript
// 在 beforeSend 中过滤
if (error.message.includes('Network Error')) {
  return null // 不发送到 Sentry
}
```

### 4. 版本管理

为每个发布版本设置标识：
```javascript
// 在环境变量中设置
VITE_APP_VERSION=1.2.3
```

## 免费版限制

- **错误数量**: 每月 5,000 个错误
- **性能数据**: 每月 100 个事务
- **用户数量**: 无限制
- **保留时间**: 30 天

对于个人项目和小型应用，免费版通常足够使用。

## 故障排除

### 错误没有上报

1. 检查 DSN 是否正确配置
2. 确认环境变量已设置
3. 检查网络连接
4. 查看浏览器控制台是否有错误

### 性能数据缺失

1. 确认 `BrowserTracing` 集成已启用
2. 检查 `tracesSampleRate` 设置
3. 确认路由配置正确

### 敏感信息泄露

1. 检查 `beforeBreadcrumb` 配置
2. 确认 URL 参数过滤
3. 避免在错误信息中包含敏感数据

## 升级建议

当项目规模扩大时，可以考虑：
- 升级到付费版获得更多配额
- 配置错误告警通知
- 集成 Slack/邮件通知
- 设置错误自动分配规则 