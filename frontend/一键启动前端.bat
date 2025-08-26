@echo off
chcp 65001 >nul
echo ========================================
echo 🚀 一键启动前端服务
echo ========================================

cd /d "%~dp0"

echo 📍 当前目录: %CD%
echo 🔍 检查dist目录...

if not exist "dist\index.html" (
    echo ❌ 错误: dist目录不存在或index.html文件缺失
    echo 💡 请先运行: npm run build
    echo.
    pause
    exit /b 1
)

echo ✅ dist目录检查通过
echo 🛑 停止所有Node.js进程...
taskkill /F /IM node.exe >nul 2>&1

echo ⏳ 等待进程完全停止...
timeout /t 3 /nobreak >nul

echo 🚀 启动强大的前端服务器...
echo.
echo 📍 访问地址: http://localhost:8081
echo 🌐 支持路由: /, /login, /register
echo ⏹️  按 Ctrl+C 停止服务器
echo.

REM 启动强大的Node.js服务器
node robust-server.js 