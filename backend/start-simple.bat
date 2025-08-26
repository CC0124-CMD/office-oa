@echo off
chcp 65001 >nul
echo ========================================
echo 启动后端服务 - 简单版
echo ========================================
echo.

echo 当前目录: %CD%
echo.

echo 直接启动后端服务...
echo 服务将在 http://localhost:8080 启动
echo H2数据库控制台: http://localhost:8080/h2-console
echo 按 Ctrl+C 停止服务
echo.

mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause 