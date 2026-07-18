# 业绩日报系统 (Daily Report)

Spring Boot 3.2 + 微信小程序 全栈项目，用于填写和汇总每日业绩指标。

---

## 📋 功能概览

| 页面 | 功能 |
|------|------|
| **今日记录** | 填写各项指标数值 → 保存 → 按模版生成总结 → 一键复制 |
| **历史记录** | 按月查看历史日报，支持编辑和删除 |
| **设置** | 自定义指标名称/单位，编辑总结模版（支持占位符），配置后端 API 地址 |

---

## 🏗 架构

```
微信小程序                    后端 (Spring Boot 3.2)
┌───────────┐    HTTP/REST    ┌──────────────┐    SQL     ┌──────────┐
│ pages/     │ ◄────────────► │ Controller   │ ◄──────► │ SQLite   │
│ index/     │   (wx.request) │ → Service    │  (MyBatis) │ / MySQL  │
│ history/   │                │ → Mapper     │           │          │
│ settings/  │                │              │           │ (4 张表) │
└───────────┘                └──────────────┘           └──────────┘
```

### 技术栈

| 层次 | 技术 |
|------|------|
| **后端** | Spring Boot 3.2 / Java 17 / MyBatis-Plus 3.5.5 |
| **前端** | 微信小程序 (WXML + WXSS + JS) |
| **数据库** | SQLite（开发） / MySQL（生产） |
| **构建** | Maven |
| **部署** | Docker 多阶段构建 |

### 数据库设计

4 张表，指标定义与记录值 **EAV 模式分离存储**，支持动态自定义指标：

```
metric_definitions ← 定义有哪些指标
daily_records      ← 日报主表（按日期唯一）
record_metrics     ← 日报的指标值（一对多关联 daily_records）
templates          ← 总结模版（唯一默认模版）
```

---

## 🚀 快速开始（本地开发）

### 前置条件

- JDK 17+
- Maven 3.8+
- 微信开发者工具

### 1. 启动后端

```bash
cd backend
mvn package -DskipTests
mvn spring-boot:run
# 或 java -jar target/daily-performance-1.0.0.jar
```

- 默认使用 `dev` profile，自动连接 `data/daily.db`（SQLite）
- 首次启动自动建表，无需手动操作
- 端口：`http://localhost:8080`

### 2. 启动小程序

1. 微信开发者工具打开 `miniprogram/` 目录
2. 设置 → 代理 → **勾选"不校验合法域名"**
3. 确认 `app.js` 中 `baseUrl: 'http://localhost:8080'`
4. 编译运行

---

## ☁️ 部署到服务器

### 方式一：自托管服务器（Docker）

以 `106.13.109.237` 为例：

#### ① 安装 Docker

```bash
ssh root@106.13.109.237
curl -fsSL https://get.docker.com | sh
```

#### ② 启动 MySQL

```bash
docker network create daily-net

docker run -d \
  --name daily-mysql \
  --network daily-net \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=your_root_password \
  -e MYSQL_DATABASE=daily_performance \
  -e MYSQL_USER=daily_user \
  -e MYSQL_PASSWORD=your_db_password \
  -v /root/mysql-data:/var/lib/mysql \
  mysql:8 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

导入表结构：

```bash
# 方式 A：复制 sql 文件到容器执行
docker cp backend/src/main/resources/schema-mysql.sql daily-mysql:/tmp/
docker exec daily-mysql mysql -uroot -pyour_root_password daily_performance < /tmp/schema-mysql.sql
```

#### ③ 构建后端镜像

```bash
cd backend
docker build -t daily-backend:1.0 .
```

#### ④ 运行后端

```bash
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

验证：`curl http://localhost:80/health`

#### ⑤ 配置小程序

在微信开发者工具中将 API 地址设为 `http://106.13.109.237`（或在小程序设置 Tab 中修改）。

> ⚠️ 正式上线建议配 HTTPS（Nginx 反代 + SSL 证书）。

### 方式二：腾讯云 CloudRun

参考 `Dockerfile` 和 `application-cloud.yml`，推送到腾讯云镜像仓库后部署。

---

## 🔧 环境变量说明

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `dev`（本地）/ `cloud`（Docker） | 激活配置 |
| `PORT` | `80` | 服务端口 |
| `MYSQL_HOST` | — | MySQL 地址 |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `MYSQL_DB` | `daily_performance` | 数据库名 |
| `MYSQL_USER` | — | MySQL 用户名 |
| `MYSQL_PASSWORD` | — | MySQL 密码 |

---

## 📁 目录结构

```
daily/
├── README.md                    # 本文件
├── .gitignore
├── backend/                     # Spring Boot 后端
│   ├── Dockerfile               # Docker 多阶段构建
│   ├── pom.xml                  # Maven 依赖管理
│   ├── readme.md                # 部署命令备忘
│   └── src/
│       ├── main/java/com/daily/
│       │   ├── DailyApplication.java     # 入口
│       │   ├── config/                   # CORS、数据源配置
│       │   ├── controller/               # REST 控制器 (4)
│       │   ├── entity/                   # 数据实体 (4)
│       │   ├── mapper/                   # MyBatis-Plus Mapper (4)
│       │   └── service/                  # 业务逻辑 (3)
│       └── main/resources/
│           ├── application.yml           # 公共配置
│           ├── application-dev.yml       # 本地 (SQLite)
│           ├── application-cloud.yml     # 生产 (MySQL)
│           └── schema*.sql               # 建表脚本
│
└── miniprogram/                 # 微信小程序前端
    ├── app.js / app.json / app.wxss
    ├── utils/api.js             # HTTP 请求封装
    └── pages/
        ├── index/               # 今日记录 Tab
        ├── history/             # 历史记录 Tab
        └── settings/            # 设置 Tab
```

---

## ℹ️ 说明

- **配置修复**：`application-cloud.yml` 第 7 行原为 `password: ${MY*********ORD}`，已修正为 `MYSQL_PASSWORD` 环境变量读取
- **模版占位符**：支持 `{name}`、`{name:值}`、`{name:单位}`、`{date}`
- **响应格式**：所有接口返回 `{"code":0, "data":...}`
