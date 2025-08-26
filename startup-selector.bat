@echo off
title Smart OA Startup Script Selector
echo ========================================
echo    Smart OA Startup Script Selector
echo ========================================
echo.
echo Please choose startup method:
echo.
echo [1] Quick Start System (Recommended)
echo     Automatically start frontend and backend services
echo.
echo [2] Start Backend Service Only
echo     Choose backend startup method
echo.
echo [3] Start Frontend Service Only
echo     Choose frontend startup method
echo.
echo [4] Test Registration Function
echo     Run registration test script
echo.
echo [5] View Startup Instructions
echo     Open startup instruction document
echo.
echo [0] Exit
echo.
set /p choice="Please enter choice (0-5): "

if "%choice%"=="1" (
    echo.
    echo Starting quick start system...
    start "Quick Start System" "quick-start.bat"
) else if "%choice%"=="2" (
    echo.
    echo Choose backend startup method:
    echo [1] Batch script (start-backend-direct.bat)
    echo [2] PowerShell script (start-backend.ps1) - Recommended
    echo.
    set /p backend_choice="Please choose (1/2): "
    if "%backend_choice%"=="1" (
        echo Using batch script to start backend...
        cd /d "%~dp0backend"
        start "Backend Service" cmd /k "start-backend-direct.bat"
    ) else if "%backend_choice%"=="2" (
        echo Using PowerShell script to start backend...
        cd /d "%~dp0backend"
        start "Backend Service" powershell -ExecutionPolicy Bypass -File "start-backend.ps1"
    ) else (
        echo Invalid choice, using default method...
        cd /d "%~dp0backend"
        start "Backend Service" powershell -ExecutionPolicy Bypass -File "start-backend.ps1"
    )
) else if "%choice%"=="3" (
    echo.
    echo Choose frontend startup method:
    echo [1] Batch script (启动.bat)
    echo [2] PowerShell script (启动.ps1) - Recommended
    echo.
    set /p frontend_choice="Please choose (1/2): "
    if "%frontend_choice%"=="1" (
        echo Using batch script to start frontend...
        cd /d "%~dp0frontend"
        start "Frontend Service" cmd /k "启动.bat"
    ) else if "%frontend_choice%"=="2" (
        echo Using PowerShell script to start frontend...
        cd /d "%~dp0frontend"
        start "Frontend Service" powershell -ExecutionPolicy Bypass -File "启动.ps1"
    ) else (
        echo Invalid choice, using default method...
        cd /d "%~dp0frontend"
        start "Frontend Service" cmd /k "启动.bat"
    )
) else if "%choice%"=="4" (
    echo.
    echo Starting registration test...
    start "Test Registration" powershell -ExecutionPolicy Bypass -File "测试注册功能.ps1"
) else if "%choice%"=="5" (
    echo.
    echo Opening startup instruction document...
    start "Startup Instructions" "启动说明.md"
) else if "%choice%"=="0" (
    echo.
    echo Exiting program...
    exit /b 0
) else (
    echo.
    echo Invalid choice, please run program again
    pause
    exit /b 1
)

echo.
echo Startup command executed!
echo Please check the newly opened window
echo.
pause 