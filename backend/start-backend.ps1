# 后端服务启动脚本 - PowerShell版本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动后端服务 - PowerShell版本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "当前目录: $PWD" -ForegroundColor Yellow
Write-Host ""

# 检查是否在正确的目录
if (-not (Test-Path "pom.xml")) {
    Write-Host "错误: 未找到pom.xml文件" -ForegroundColor Red
    Write-Host "当前目录：$PWD" -ForegroundColor Red
    Write-Host "请确保此脚本在backend目录中运行" -ForegroundColor Red
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "确认在backend目录：✓" -ForegroundColor Green
Write-Host ""

# 检查Java环境
Write-Host "检查Java环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java环境检查通过：✓" -ForegroundColor Green
} catch {
    Write-Host "错误: 未找到Java环境" -ForegroundColor Red
    Write-Host "请确保Java已安装并添加到PATH环境变量" -ForegroundColor Red
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}

Write-Host ""

# 检查Maven环境
Write-Host "检查Maven环境..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "Maven环境检查通过：✓" -ForegroundColor Green
} catch {
    Write-Host "错误: 未找到Maven环境" -ForegroundColor Red
    Write-Host "请确保Maven已安装并添加到PATH环境变量" -ForegroundColor Red
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}

Write-Host ""

# 检查8080端口是否被占用
Write-Host "检查8080端口是否被占用..." -ForegroundColor Yellow
$portCheck = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($portCheck) {
    Write-Host "警告：端口8080已被占用！" -ForegroundColor Yellow
    Write-Host "正在尝试清理端口..." -ForegroundColor Yellow
    foreach ($connection in $portCheck) {
        try {
            Stop-Process -Id $connection.OwningProcess -Force -ErrorAction SilentlyContinue
            Write-Host "关闭进程 $($connection.OwningProcess)" -ForegroundColor Yellow
        } catch {
            Write-Host "无法关闭进程 $($connection.OwningProcess)" -ForegroundColor Red
        }
    }
    Start-Sleep -Seconds 2
    Write-Host "端口清理完成" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动后端服务..." -ForegroundColor Cyan
Write-Host "服务地址: http://localhost:8080" -ForegroundColor Cyan
Write-Host "H2数据库控制台: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host "按 Ctrl+C 停止服务" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "正在启动Spring Boot应用..." -ForegroundColor Green
try {
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
} catch {
    Write-Host "启动失败: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "后端服务已停止" -ForegroundColor Yellow
Read-Host "按回车键退出" 