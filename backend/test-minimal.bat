@echo off
echo ========================================
echo 最小化环境测试
echo ========================================
echo.

echo 当前目录: %CD%
echo.

echo 测试Java...
java -version
echo.

echo 测试Maven...
mvn -version
echo.

echo 测试完成
pause 