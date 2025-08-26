#!/bin/bash

# 智能OA系统部署脚本
# 作者：陈志培
# 版本：1.0.0

echo "=========================================="
echo "智能OA系统部署脚本"
echo "作者：陈志培"
echo "版本：1.0.0"
echo "=========================================="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "错误：Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误：Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 创建必要的目录
echo "创建必要的目录..."
mkdir -p logs
mkdir -p data/mysql
mkdir -p data/redis
mkdir -p data/minio

# 复制数据库初始化脚本
echo "复制数据库初始化脚本..."
cp docs/数据库设计/smart_oa.sql docker/init.sql

# 构建并启动服务
echo "构建并启动服务..."
cd docker
docker-compose up -d --build

# 等待服务启动
echo "等待服务启动..."
sleep 30

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

# 显示访问信息
echo "=========================================="
echo "部署完成！"
echo "=========================================="
echo "系统访问地址："
echo "前端界面：http://localhost:8081"
echo "后端API：http://localhost:8080/api"
echo "MinIO控制台：http://localhost:9001"
echo "数据库：localhost:3306"
echo "Redis：localhost:6379"
echo ""
echo "默认账号："
echo "管理员：admin / 123456"
echo "普通用户：zhangsan / 123456"
echo ""
echo "查看日志：docker-compose logs -f"
echo "停止服务：docker-compose down"
echo "==========================================" 