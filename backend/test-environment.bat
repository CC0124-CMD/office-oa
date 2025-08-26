@echo off
chcp 65001 >nul
echo ========================================
echo 环境测试脚本 - 增强版
echo ========================================
echo.

echo 当前目录: %CD%
echo.

echo 测试1: 检查Java环境
echo ------------------------
echo 正在检查Java...
java -version
if errorlevel 1 (
    echo 错误: Java检查失败
    echo 请确保Java已安装并添加到PATH环境变量
) else (
    echo Java检查完成 - 成功
)
echo.

echo 测试2: 检查Maven环境
echo ------------------------
echo 正在检查Maven...
mvn -version
if errorlevel 1 (
    echo 错误: Maven检查失败
    echo 请确保Maven已安装并添加到PATH环境变量
) else (
    echo Maven检查完成 - 成功
)
echo.

echo 测试3: 检查pom.xml文件
echo ------------------------
if exist "pom.xml" (
    echo pom.xml文件存在 - 成功
) else (
    echo 错误: pom.xml文件不存在
    echo 请确保在backend目录中运行此脚本
)
echo.

echo 测试4: 检查target目录
echo ------------------------
if exist "target" (
    echo target目录存在
) else (
    echo target目录不存在（这是正常的，如果还没有编译过）
)
echo.

echo 测试5: 尝试编译项目
echo ------------------------
echo 正在执行: mvn clean compile -q
echo 这可能需要几分钟时间，请耐心等待...
mvn clean compile -q
if errorlevel 1 (
    echo 编译失败
    echo 请检查项目配置或网络连接
) else (
    echo 编译成功
)
echo.

echo 所有测试完成
echo 按任意键退出...
pause >nul 