@echo off
chcp 65001 >nul
echo ========================================
echo 🚀 启动后端服务
echo ========================================

cd /d "%~dp0"

echo 📍 当前目录: %CD%
echo 🔍 检查Java环境...

java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Java环境
    echo 💡 请安装Java 8或更高版本
    pause
    exit /b 1
)

echo ✅ Java环境检查通过
echo 🔍 检查Maven环境...

mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Maven环境
    echo 💡 请安装Maven
    pause
    exit /b 1
)

echo ✅ Maven环境检查通过
echo 🛑 停止现有的Java进程...
taskkill /F /IM java.exe >nul 2>&1

echo ⏳ 等待进程完全停止...
timeout /t 3 /nobreak >nul

echo 🏗️  构建后端项目...
mvn clean package -DskipTests

if errorlevel 1 (
    echo ❌ 构建失败
    pause
    exit /b 1
)

echo ✅ 构建成功
echo 🚀 启动后端服务器...
echo.
echo 📍 服务地址: http://localhost:8080
echo 🔧 API文档: http://localhost:8080/test/health
echo ⏹️  按 Ctrl+C 停止服务器
echo.

java -jar target/smart-oa-minimal-1.0.0.jar 