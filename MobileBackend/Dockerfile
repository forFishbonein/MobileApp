# ==========================
# 第一阶段：Build Stage
# ==========================
FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /app

# 1. 拷贝父级pom.xml
COPY ./pom.xml ./

# 2. 拷贝各子模块的pom.xml（便于go-offline缓存依赖）
COPY tutoring-common/pom.xml  ./tutoring-common/
COPY tutoring-pojo/pom.xml    ./tutoring-pojo/
COPY tutoring-server/pom.xml  ./tutoring-server/

# 3. 先下载依赖
RUN mvn dependency:go-offline

# 4. 再拷贝全部源码
COPY . .

# 5. 编译打包
RUN mvn clean package -DskipTests

# ==========================
# 第二阶段：Runtime Stage
# ==========================
FROM openjdk:8-jre-slim
WORKDIR /app

# 关键：一定要写对Jar文件的实际名字
COPY --from=builder /app/tutoring-server/target/tutoring-server-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]



