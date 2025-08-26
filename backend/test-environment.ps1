# 设置控制台编码为UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "环境测试脚本 - PowerShell版本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "当前目录: $PWD" -ForegroundColor White
Write-Host ""

Write-Host "测试1: 检查Java环境" -ForegroundColor Yellow
Write-Host "------------------------" -ForegroundColor Gray
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "Java版本: $javaVersion" -ForegroundColor Green
        Write-Host "Java检查完成 - 成功" -ForegroundColor Green
    } else {
        throw "Java not found"
    }
} catch {
    Write-Host "错误: Java检查失败" -ForegroundColor Red
    Write-Host "请确保Java已安装并添加到PATH环境变量" -ForegroundColor Red
}

Write-Host ""
Write-Host "测试2: 检查Maven环境" -ForegroundColor Yellow
Write-Host "------------------------" -ForegroundColor Gray
try {
    $mvnVersion = mvn -version 2>$null | Select-String "Apache Maven"
    if ($mvnVersion) {
        Write-Host "Maven版本: $mvnVersion" -ForegroundColor Green
        Write-Host "Maven检查完成 - 成功" -ForegroundColor Green
    } else {
        throw "Maven not found"
    }
} catch {
    Write-Host "错误: Maven检查失败" -ForegroundColor Red
    Write-Host "请确保Maven已安装并添加到PATH环境变量" -ForegroundColor Red
}

Write-Host ""
Write-Host "测试3: 检查pom.xml文件" -ForegroundColor Yellow
Write-Host "------------------------" -ForegroundColor Gray
if (Test-Path "pom.xml") {
    Write-Host "pom.xml文件存在 - 成功" -ForegroundColor Green
} else {
    Write-Host "错误: pom.xml文件不存在" -ForegroundColor Red
    Write-Host "请确保在backend目录中运行此脚本" -ForegroundColor Red
}

Write-Host ""
Write-Host "测试4: 检查target目录" -ForegroundColor Yellow
Write-Host "------------------------" -ForegroundColor Gray
if (Test-Path "target") {
    Write-Host "target目录存在" -ForegroundColor White
} else {
    Write-Host "target目录不存在（这是正常的，如果还没有编译过）" -ForegroundColor White
}

Write-Host ""
Write-Host "测试5: 尝试编译项目" -ForegroundColor Yellow
Write-Host "------------------------" -ForegroundColor Gray
Write-Host "正在执行: mvn clean compile -q" -ForegroundColor White
Write-Host "这可能需要几分钟时间，请耐心等待..." -ForegroundColor White
try {
    mvn clean compile -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "编译成功" -ForegroundColor Green
    } else {
        throw "Compilation failed"
    }
} catch {
    Write-Host "编译失败" -ForegroundColor Red
    Write-Host "请检查项目配置或网络连接" -ForegroundColor Red
}

Write-Host ""
Write-Host "所有测试完成" -ForegroundColor Cyan
Read-Host "按回车键退出" 