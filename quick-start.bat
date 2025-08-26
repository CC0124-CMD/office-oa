@echo off
title Smart OA System Quick Start
echo ========================================
echo Smart OA System Quick Start
echo ========================================
echo.

echo Starting system...
echo.

echo 1. Starting Backend Service...
echo Starting backend service...
cd /d "%~dp0backend"

echo Choose backend startup method:
echo [1] Use batch script (start-backend-direct.bat)
echo [2] Use PowerShell script (start-backend.ps1) - Recommended
echo [3] Auto-select
echo.
set /p choice="Please choose (1/2/3, default 3): "

if "%choice%"=="1" (
    echo Using batch script to start backend...
    start "Backend Service" cmd /k "start-backend-direct.bat"
) else if "%choice%"=="2" (
    echo Using PowerShell script to start backend...
    start "Backend Service" powershell -ExecutionPolicy Bypass -File "start-backend.ps1"
) else (
    echo Auto-selecting startup method...
    echo Trying PowerShell script first...
    start "Backend Service" powershell -ExecutionPolicy Bypass -File "start-backend.ps1"
    if errorlevel 1 (
        echo PowerShell startup failed, trying batch script...
        start "Backend Service" cmd /k "start-backend-direct.bat"
    )
)

echo Waiting for backend to start...
timeout /t 5 /nobreak >nul

echo.
echo 2. Starting Frontend Service...
echo Starting frontend service...
cd /d "%~dp0frontend"

echo Choose frontend startup method:
echo [1] Use batch script (启动.bat)
echo [2] Use PowerShell script (启动.ps1) - Recommended
echo [3] Auto-select
echo.
set /p choice2="Please choose (1/2/3, default 3): "

if "%choice2%"=="1" (
    echo Using batch script to start frontend...
    start "Frontend Service" cmd /k "启动.bat"
) else if "%choice2%"=="2" (
    echo Using PowerShell script to start frontend...
    start "Frontend Service" powershell -ExecutionPolicy Bypass -File "启动.ps1"
) else (
    echo Auto-selecting startup method...
    echo Trying PowerShell script first...
    start "Frontend Service" powershell -ExecutionPolicy Bypass -File "启动.ps1"
    if errorlevel 1 (
        echo PowerShell startup failed, trying batch script...
        start "Frontend Service" cmd /k "启动.bat"
    )
)

echo.
echo ========================================
echo System startup completed!
echo ========================================
echo.
echo Backend Service: http://localhost:8080
echo Frontend Service: http://localhost:8081
echo H2 Database: http://localhost:8080/h2-console
echo.
echo If services fail to start, please check:
echo 1. Java and Maven are properly installed
echo 2. Ports 8080 and 8081 are not occupied
echo 3. Project dependencies are correct
echo.
echo Test registration: Run 测试注册功能.ps1
echo.
pause 