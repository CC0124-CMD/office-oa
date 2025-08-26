@echo off
title Backend Service (JAR)
echo ========================================
echo Starting Backend Service (JAR)
echo ========================================
echo.

echo Current directory: %CD%
echo.

echo Checking if in correct directory...
if not exist "target\smart-oa-system-1.0.0.jar" (
    echo ERROR: JAR file not found!
    echo Current directory: %CD%
    echo Please ensure this script is run in the backend directory
    echo.
    pause
    exit /b 1
)

echo Confirmed JAR file exists: ✓
echo.

echo Checking Java environment...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found
    echo Please ensure Java is installed and added to PATH
    echo.
    pause
    exit /b 1
)
echo Java environment check passed: ✓
echo.

echo Checking if port 8080 is occupied...
netstat -ano | findstr :8080 >nul 2>&1
if errorlevel 0 (
    echo WARNING: Port 8080 is occupied!
    echo Trying to clean up port...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
        echo Closing process %%a
        taskkill /f /pid %%a >nul 2>&1
    )
    timeout /t 2 /nobreak >nul
    echo Port cleanup completed
)

echo.
echo ========================================
echo Starting Backend Service...
echo Service URL: http://localhost:8080
echo H2 Database Console: http://localhost:8080/h2-console
echo Press Ctrl+C to stop service
echo ========================================
echo.

echo Starting Spring Boot application from JAR...
java -jar target\smart-oa-system-1.0.0.jar

echo.
echo Backend service stopped
pause 