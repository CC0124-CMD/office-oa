Write-Host "========================================" -ForegroundColor Green
Write-Host "🚀 一键启动前端服务" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 设置当前目录
Set-Location $PSScriptRoot

Write-Host "📍 当前目录: $(Get-Location)" -ForegroundColor Yellow
Write-Host "🔍 检查dist目录..." -ForegroundColor Yellow

# 检查dist目录是否存在
if (-not (Test-Path "dist\index.html")) {
    Write-Host "❌ 错误: dist目录不存在或index.html文件缺失" -ForegroundColor Red
    Write-Host "💡 请先运行: npm run build" -ForegroundColor Red
    Write-Host ""
    Read-Host "按任意键继续"
    exit 1
}

Write-Host "✅ dist目录检查通过" -ForegroundColor Green

# 停止现有的Node.js进程
Write-Host "🛑 停止所有Node.js进程..." -ForegroundColor Yellow
Get-Process -Name "node" -ErrorAction SilentlyContinue | Stop-Process -Force

# 等待进程完全停止
Write-Host "⏳ 等待进程完全停止..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# 启动前端服务器
Write-Host "🚀 启动强大的前端服务器..." -ForegroundColor Green
Write-Host ""
Write-Host "📍 访问地址: http://localhost:8081" -ForegroundColor Cyan
Write-Host "🌐 支持路由: /, /login, /register" -ForegroundColor Cyan
Write-Host "⏹️  按 Ctrl+C 停止服务器" -ForegroundColor Yellow
Write-Host ""

# 启动强大的Node.js服务器
& node robust-server.js 