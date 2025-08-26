@echo off
chcp 65001 >nul
echo ========================================
echo 启动后端服务 - PowerShell版本
echo ========================================
echo.

echo 正在启动PowerShell脚本...
powershell -ExecutionPolicy Bypass -File "start-backend.ps1"

echo.
echo PowerShell脚本执行完成
pause 