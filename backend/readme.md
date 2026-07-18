# Backend — 业绩日报后端

## 本地运行

```bash
mvn spring-boot:run
# 默认 dev profile，SQLite 数据库
```

## 部署到 106.13.109.237

```bash
# 服务器上构建
ssh root@106.13.109.237
cd /root/daily/backend
docker build -t daily-backend:1.0 .

# 运行（需先启动 MySQL 容器 daily-mysql）
docker run -d \
  --name daily-backend \
  --network daily-net \
  -p 80:80 \
  -e SPRING_PROFILES_ACTIVE=cloud \
  -e MYSQL_HOST=daily-mysql \
  -e MYSQL_DB=daily_performance \
  -e MYSQL_USER=daily_user \
  -e MYSQL_PASSWORD=your_db_password \
  daily-backend:1.0
```

## Tencent Cloud CloudRun

```bash
docker login ccr.ccs.tencentyun.com --username=100001626440
docker build -t daily-performance:1.0 .
docker tag daily-performance:1.0 ccr.ccs.tencentyun.com/chendaofu/daily-performance:1.0
docker push ccr.ccs.tencentyun.com/chendaofu/daily-performance:1.0
```
