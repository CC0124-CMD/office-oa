Param(
  [switch]$FullClean = $false,
  [switch]$SkipInstall = $false,
  [string]$Path = 'frontend'
)

$ErrorActionPreference = 'Stop'

function Write-Step($msg) {
  Write-Host "==> $msg" -ForegroundColor Cyan
}

function Ensure-Tool($cmd, $name) {
  if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
    throw "$name 未安装或不在 PATH 中：$cmd"
  }
}

try {
  Write-Step '检查 Node 与 npm 环境'
  Ensure-Tool node 'Node.js'
  Ensure-Tool npm 'npm'
  node -v
  npm -v

  # 解析目标目录（支持绝对/相对路径）
  $scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
  if ([System.IO.Path]::IsPathRooted($Path)) {
    $frontendDir = $Path
  } else {
    $frontendDir = Join-Path $scriptRoot $Path
  }
  if (-not (Test-Path $frontendDir)) { throw "未找到目录：$frontendDir" }
  Set-Location $frontendDir
  Write-Step "进入目录：$((Get-Location).Path)"

  # 可选：终止已有 serve 进程（避免占用资源）
  Write-Step '尝试终止可能残留的开发进程（忽略失败）'
  Get-Process node -ErrorAction SilentlyContinue | Where-Object { $_.Path -like '*node*' -and ($_.MainWindowTitle -like '*vue*' -or $_.ProcessName -like '*vue*') } | Stop-Process -Force -ErrorAction SilentlyContinue

  if ($FullClean) {
    Write-Step '执行完整清理（node_modules、package-lock.json、dist）'
    if (Test-Path 'node_modules') { Remove-Item 'node_modules' -Recurse -Force }
    if (Test-Path 'package-lock.json') { Remove-Item 'package-lock.json' -Force }
  } else {
    Write-Step '执行基础清理（dist）'
  }
  if (Test-Path 'dist') { Remove-Item 'dist' -Recurse -Force }

  if (-not $SkipInstall) {
    Write-Step '安装依赖（npm ci 若失败则回退 npm install）'
    try {
      if (Test-Path 'package-lock.json') {
        npm ci --no-fund --no-audit
      } else {
        throw 'no lockfile'
      }
    } catch {
      Write-Host 'npm ci 失败，回退到 npm install' -ForegroundColor Yellow
      npm install --no-fund --no-audit
    }
  } else {
    Write-Step '跳过依赖安装（按需使用 --SkipInstall）'
  }

  Write-Step '开始生产构建（npm run build）'
  npm run build

  Write-Step '构建完成，输出目录：dist'
  exit 0
} catch {
  Write-Host "构建失败：$($_.Exception.Message)" -ForegroundColor Red
  if ($_.ScriptStackTrace) { Write-Host $_.ScriptStackTrace -ForegroundColor DarkGray }
  exit 1
}