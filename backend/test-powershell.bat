@echo off
chcp 65001 >nul
echo ========================================
echo 环境测试 - PowerShell版本
echo ========================================
echo.

echo 正在启动PowerShell测试脚本...
powershell -ExecutionPolicy Bypass -File "test-environment.ps1"

echo.
echo PowerShell测试脚本执行完成
pause 