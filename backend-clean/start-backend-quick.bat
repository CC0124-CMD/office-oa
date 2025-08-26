@echo off
chcp 65001 >nul
echo ========================================
echo ⚡ 快速启动后端服务
echo ========================================

cd /d "%~dp0"

echo 📍 当前目录: %CD%

if not exist "target\smart-oa-minimal-1.0.0.jar" (
    echo ❌ 错误: 未找到JAR文件
    echo 💡 请先运行: start-backend-direct.bat
    pause
    exit /b 1
)

echo ✅ JAR文件检查通过
echo 🛑 停止现有的Java进程...
taskkill /F /IM java.exe >nul 2>&1

echo ⏳ 等待进程完全停止...
timeout /t 2 /nobreak >nul

echo 🚀 启动后端服务器...
echo.
echo 📍 服务地址: http://localhost:8080
echo 🔧 API文档: http://localhost:8080/test/health
echo ⏹️  按 Ctrl+C 停止服务器
echo.

java -jar target/smart-oa-minimal-1.0.0.jar 