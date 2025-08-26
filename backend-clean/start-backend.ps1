Write-Host "========================================" -ForegroundColor Green
Write-Host "🚀 启动后端服务" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 设置当前目录
Set-Location $PSScriptRoot

Write-Host "📍 当前目录: $(Get-Location)" -ForegroundColor Yellow
Write-Host "🔍 检查Java环境..." -ForegroundColor Yellow

# 检查Java环境
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java环境检查通过" -ForegroundColor Green
} catch {
    Write-Host "❌ 错误: 未找到Java环境" -ForegroundColor Red
    Write-Host "💡 请安装Java 8或更高版本" -ForegroundColor Red
    Read-Host "按任意键继续"
    exit 1
}

Write-Host "🔍 检查Maven环境..." -ForegroundColor Yellow

# 检查Maven环境
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "✅ Maven环境检查通过" -ForegroundColor Green
} catch {
    Write-Host "❌ 错误: 未找到Maven环境" -ForegroundColor Red
    Write-Host "💡 请安装Maven" -ForegroundColor Red
    Read-Host "按任意键继续"
    exit 1
}

# 停止现有的Java进程
Write-Host "🛑 停止现有的Java进程..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force

# 等待进程完全停止
Write-Host "⏳ 等待进程完全停止..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# 构建项目
Write-Host "🏗️  构建后端项目..." -ForegroundColor Yellow
& mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 构建失败" -ForegroundColor Red
    Read-Host "按任意键继续"
    exit 1
}

Write-Host "✅ 构建成功" -ForegroundColor Green

# 启动服务器
Write-Host "🚀 启动后端服务器..." -ForegroundColor Green
Write-Host ""
Write-Host "📍 服务地址: http://localhost:8080" -ForegroundColor Cyan
Write-Host "🔧 API文档: http://localhost:8080/test/health" -ForegroundColor Cyan
Write-Host "⏹️  按 Ctrl+C 停止服务器" -ForegroundColor Yellow
Write-Host ""

& java -jar target/smart-oa-minimal-1.0.0.jar 