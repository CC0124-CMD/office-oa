# 智能OA系统

## 项目简介
智能OA系统是一个基于Spring Boot + Vue.js的现代化办公自动化系统，提供完整的审批流程管理功能。

## 快速启动

### 方式一：完整服务启动（推荐）
运行 `启动服务.bat` 脚本，将同时启动前端和后端服务：
- 前端服务：http://localhost:8081
- 后端API：http://localhost:8080/api

### 方式二：仅启动前端服务
运行 `启动前端.bat` 脚本，仅启动前端服务：
- 前端服务：http://localhost:8081

### 方式三：演示页面
直接打开 `demo.html` 文件查看系统演示。

## 系统要求
- Java 8+
- Maven 3.6+
- Node.js (用于http-server)

## 安装依赖
如果http-server未安装，请运行：
```bash
npm install -g http-server
```

## 测试账号
- 管理员：admin / 123456
- 普通用户：zhangsan / 123456

## 项目结构
```
smart-oa-system/
├── backend/          # Spring Boot后端
├── frontend/         # Vue.js前端
├── demo.html         # 演示页面
├── 启动服务.bat      # 完整服务启动脚本
├── 启动前端.bat      # 前端服务启动脚本
└── start.bat         # 演示页面启动脚本
```

## 常见问题

### 1. 前端页面显示目录列表而不是登录界面
**解决方案：**
- 确保运行的是 `启动服务.bat` 或 `启动前端.bat`
- 检查http-server是否在frontend目录中启动
- 访问 http://localhost:8081 而不是其他端口

### 2. 后端服务启动失败
**解决方案：**
- 检查Java和Maven是否正确安装
- 确保8080端口未被占用
- 查看后端控制台错误信息

### 3. 前端服务启动失败
**解决方案：**
- 安装http-server：`npm install -g http-server`
- 确保8081端口未被占用
- 检查frontend目录是否存在

## 技术栈
- **后端：** Spring Boot, Spring Security, MyBatis
- **前端：** Vue.js 3, Element Plus
- **数据库：** MySQL
- **构建工具：** Maven, npm

## 开发团队
- 作者：陈志培
- 版本：1.0.0 