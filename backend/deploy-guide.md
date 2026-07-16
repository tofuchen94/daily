# 后端服务部署指南

## 📋 服务器信息（按需替换为你的实际值）

| 项目 | 示例值 | 说明 |
|---|---|---|
| 服务器 IP | `your-server-ip` | 你的云服务器公网 IP |
| SSH 用户 | `root` | SSH 登录用户名 |
| 后端端口 | `8080` | 映射到容器内 `80` |
| MySQL 容器名 | `mysql-server` | 需在 `daily-net` 网络中 |

## 🔧 前置条件

- 本地：**JDK 17**、**Maven**、**SSH Key** 已配置
- 服务器：**Docker** 已安装，MySQL 容器已在运行

---

## 🚀 完整部署步骤

### 第一步：编译打包

```bash
# 在本地 backend 目录下执行
cd backend

# 清理并打包（跳过测试）
mvn clean package -DskipTests
```

生成文件：`target/daily-performance-1.0.0.jar`

### 第二步：上传文件到服务器

```bash
# 上传新 JAR 到服务器（替换 your-server-ip 为你的服务器 IP）
scp target/daily-performance-1.0.0.jar root@your-server-ip:/root/daily-backend/

# （可选）如果修改了 Dockerfile，也上传
scp Dockerfile.deploy root@your-server-ip:/root/daily-backend/Dockerfile
```

> **注意**：如果修改了 `application-cloud.yml` 或其他资源文件，需要重新执行第一步打包。

### 第三步：服务器上构建镜像

```bash
ssh root@your-server-ip

# 进入部署目录
cd /root/daily-backend

# 构建 Docker 镜像（服务器会自动从 Docker Hub 拉取基础镜像）
docker build -t daily-backend:latest .
```

### 第四步：停止旧容器，启动新容器

```bash
# 停止并删除旧容器
docker stop daily-backend
docker rm daily-backend

# 启动新容器（将下方占位符替换为你的实际值）
docker run -d \
  --name daily-backend \
  --restart always \
  --network daily-net \
  -p 8080:80 \
  -e MYSQL_HOST=mysql-server \
  -e MYSQL_PORT=3306 \
  -e MYSQL_DB=daily \
  -e MYSQL_USER=<你的数据库用户名> \
  -e MYSQL_PASSWORD=<你的数据库密码> \
  -e PORT=80 \
  daily-backend:latest
```

### 第五步：验证部署

```bash
# 查看容器状态
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 查看启动日志（确认无错误）
docker logs daily-backend --tail 20

# 测试健康检查接口
curl http://localhost:8080/health

# 测试 Swagger 文档
curl http://localhost:8080/v3/api-docs | head
```

正常返回 `{"status":"ok"}` 即表示部署成功。

---

## 🔄 一键部署脚本

将以下脚本中的占位符替换为实际值，在本地执行：

```bash
# 配置区 —— 请替换以下变量为你的实际值
SERVER_IP="your-server-ip"
MYSQL_HOST="mysql-server"       # MySQL 容器名（同 Docker 网络）
MYSQL_PORT="3306"
MYSQL_DB="daily"
MYSQL_USER="your-db-user"
MYSQL_PASSWORD="your-db-password"

# ====== 1. 打包 ======
cd backend
mvn clean package -DskipTests

# ====== 2. 上传 ======
scp target/daily-performance-1.0.0.jar root@${SERVER_IP}:/root/daily-backend/
scp Dockerfile.deploy root@${SERVER_IP}:/root/daily-backend/Dockerfile

# ====== 3. 服务器上构建 + 重启 ======
ssh root@${SERVER_IP} "
  cd /root/daily-backend
  docker build -t daily-backend:latest .
  docker stop daily-backend 2>/dev/null; docker rm daily-backend 2>/dev/null
  docker run -d \
    --name daily-backend \
    --restart always \
    --network daily-net \
    -p 8080:80 \
    -e MYSQL_HOST=${MYSQL_HOST} \
    -e MYSQL_PORT=${MYSQL_PORT} \
    -e MYSQL_DB=${MYSQL_DB} \
    -e MYSQL_USER=${MYSQL_USER} \
    -e MYSQL_PASSWORD=${MYSQL_PASSWORD} \
    -e PORT=80 \
    daily-backend:latest
  sleep 5
  curl -s http://localhost:8080/health
"
```

---

## ⚙️ 关键文件说明

| 文件 | 作用 |
|---|---|
| `Dockerfile` | 原始多阶段构建（本地构建用） |
| `Dockerfile.deploy` | **部署用**的简化版，仅复制已编译好的 JAR |
| `application-cloud.yml` | **生产配置**，通过 `${环境变量}` 读取 MySQL 连接信息 |
| `application-dev.yml` | **本地开发配置**，通过 `${环境变量}` 读取 MySQL 连接信息 |

> **部署时用的 `Dockerfile.deploy` 会在上传时重命名为 `Dockerfile`。**

---

## 📝 常见问题

### Q: 修改了代码怎么部署？
1. 本地 `mvn clean package -DskipTests` 重新打包
2. `scp` 上传新 JAR 到服务器
3. 服务器上 `docker build -t daily-backend:latest .` 重建镜像
4. 执行上述第四步重启容器

### Q: 本地开发怎么设置环境变量？
`application-dev.yml` 现在通过 `${DEV_MYSQL_HOST}`, `${DEV_MYSQL_USER}`, `${DEV_MYSQL_PASSWORD}` 等占位符读取敏感信息，需要在本地设置环境变量。

**方法一：IntelliJ IDEA（推荐）**
1. Run → Edit Configurations → 选择 `DailyApplication`
2. Environment variables → 填入（替换为实际值）：
```
DEV_MYSQL_HOST=<你的服务器IP>;DEV_MYSQL_PORT=3306;DEV_MYSQL_DB=daily;DEV_MYSQL_USER=<你的用户名>;DEV_MYSQL_PASSWORD=<你的密码>
```

**方法二：命令行（Git Bash）**
```bash
export DEV_MYSQL_HOST=<你的服务器IP>
export DEV_MYSQL_PORT=3306
export DEV_MYSQL_DB=daily
export DEV_MYSQL_USER=<你的用户名>
export DEV_MYSQL_PASSWORD=<你的密码>
mvn spring-boot:run
```

**方法三：IDE 启动脚本**
参考项目根目录的 `.env.example` 文件，复制为 `.env` 并填入实际值。

### Q: 端口冲突怎么办？
修改 `docker run` 中的端口映射，例如 `-p 9090:80`（外部 9090 映射到容器内 80）。

### Q: 想用服务器本地 MySQL（非容器）？
把 `-e MYSQL_HOST=mysql-server` 改为 `-e MYSQL_HOST=host.docker.internal`，并加上 `--add-host host.docker.internal:host-gateway` 参数。
