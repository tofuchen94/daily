# ========== 构建阶段 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
# 先下载依赖 (利用 Docker 缓存层)
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ========== 运行阶段 ==========
FROM eclipse-temurin:17-jre

WORKDIR /app

# 创建数据目录 (本地 SQLite 兜底)
RUN mkdir -p /app/data

# 从构建阶段复制 jar
COPY --from=builder /app/target/*.jar app.jar

# 云托管默认端口 80
ENV PORT=80
EXPOSE 80

# 激活 cloud 配置 (使用 MySQL)
ENV SPRING_PROFILES_ACTIVE=cloud

ENTRYPOINT ["java", "-jar", "app.jar"]
