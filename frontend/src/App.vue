<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 全局返回首页按钮样式 - 确保完全固定在视口上 */
.back-btn {
  position: fixed !important;
  top: 24px !important;
  left: 24px !important;
  z-index: 999999 !important;
  transform: translateZ(0) !important;
  will-change: transform !important;
  pointer-events: auto !important;
  isolation: isolate !important;
  contain: layout style paint !important;
  position: fixed !important;
  top: 24px !important;
  left: 24px !important;
  z-index: 999999 !important;
}

html, body {
  height: 100%;
  background-color: #f5f5f5;
}

.el-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #e6e6e6;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.dashboard-card {
  margin-bottom: 20px;
}

.statistics-card {
  text-align: center;
  padding: 20px;
}

.statistics-number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.statistics-label {
  font-size: 14px;
  color: #666;
  margin-top: 10px;
}

.approval-form {
  max-width: 800px;
  margin: 0 auto;
}

.task-list {
  margin-top: 20px;
}

.task-item {
  margin-bottom: 10px;
  padding: 15px;
  background: #fff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.task-item.urgent {
  border-left-color: #f56c6c;
}

.task-item.normal {
  border-left-color: #67c23a;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.task-title {
  font-weight: bold;
  color: #333;
}

.task-priority {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
}

.task-priority.urgent {
  background-color: #f56c6c;
}

.task-priority.normal {
  background-color: #67c23a;
}

.task-priority.low {
  background-color: #909399;
}

.task-info {
  color: #666;
  font-size: 14px;
}

.task-actions {
  margin-top: 10px;
}

.attendance-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.attendance-time {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin: 20px 0;
}

.attendance-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.announcement-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.announcement-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.announcement-meta {
  color: #666;
  font-size: 14px;
}

.announcement-content {
  line-height: 1.6;
  color: #333;
}

.file-manager {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.file-toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-list {
  border: 1px solid #e6e6e6;
  border-radius: 4px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.file-item:last-child {
  border-bottom: none;
}

.file-icon {
  margin-right: 10px;
  font-size: 20px;
}

.file-info {
  flex: 1;
}

.file-name {
  font-weight: bold;
  color: #333;
}

.file-meta {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.file-actions {
  display: flex;
  gap: 10px;
}

.notification-badge {
  position: relative;
}

.notification-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #f56c6c;
  color: #fff;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 