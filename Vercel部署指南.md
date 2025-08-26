# Vercel 部署指南

## 快速部署步骤

### 1. 准备项目
确保你的项目已经推送到 GitHub 仓库。

### 2. 登录 Vercel
1. 访问 [vercel.com](https://vercel.com)
2. 使用 GitHub 账号登录
3. 点击 "New Project"

### 3. 导入项目
1. 选择你的 GitHub 仓库
2. 设置项目名称（可选）
3. 保持默认配置：
   - Framework Preset: `Vue.js`
   - Root Directory: `frontend`
   - Build Command: `npm run build`
   - Output Directory: `dist`
4. 点击 "Deploy"

### 4. 配置环境变量（可选）
如果需要连接后端 API：
1. 在项目设置中找到 "Environment Variables"
2. 添加变量：
   - Name: `VITE_API_BASE`
   - Value: `https://your-backend-api.com`
3. 选择环境：Production, Preview, Development

## 自动部署

部署后，Vercel 会自动：
- 监听 GitHub 仓库的 push 事件
- 自动构建和部署新版本
- 为每个 PR 创建预览环境

## 自定义域名

1. 在项目设置中找到 "Domains"
2. 添加你的自定义域名
3. 按照提示配置 DNS 记录

## 项目配置说明

### vercel.json
- **SPA 重写**: 所有路由都重定向到 `index.html`，支持 Vue Router 的 history 模式
- **安全头**: 添加了基本的安全响应头
- **缓存策略**: 静态资源设置长期缓存

### 环境变量
- 后端 API 地址通过 `VITE_API_BASE` 环境变量配置
- 如果不设置，系统会跳过后端健康检查

## 常见问题

### Q: 部署后路由不工作？
A: 检查 `vercel.json` 中的重写规则是否正确配置。

### Q: 如何更新部署？
A: 推送代码到 GitHub 主分支，Vercel 会自动重新部署。

### Q: 如何回滚到之前的版本？
A: 在 Vercel 控制台的 "Deployments" 页面，点击任意版本可以重新部署。

### Q: 如何查看构建日志？
A: 在部署详情页面可以看到完整的构建和部署日志。

## 性能优化

Vercel 自动提供：
- 全球 CDN 分发
- 自动 HTTPS
- 图片优化
- 静态资源压缩

## 监控和分析

在 Vercel 控制台可以查看：
- 访问量统计
- 性能指标
- 错误日志
- 函数调用统计（如果使用 Serverless Functions）

## 免费版限制

- 构建时间：每月 6000 分钟
- 带宽：每月 100GB
- 函数执行：每月 100GB-Hours
- 团队协作：最多 3 个成员

对于个人项目和小型应用，免费版通常足够使用。 