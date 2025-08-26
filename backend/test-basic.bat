@echo off
chcp 65001 >nul
echo ========================================
echo 基本环境测试
echo ========================================
echo.

echo 当前目录: %CD%
echo.

echo 测试Java环境...
java -version
echo.

echo 测试Maven环境...
mvn -version
echo.

echo 测试完成
echo 按任意键退出...
pause >nul 