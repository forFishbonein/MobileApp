# ==========================
# Build Stage
# ==========================
FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /app

COPY ./pom.xml ./

COPY tutoring-common/pom.xml  ./tutoring-common/
COPY tutoring-pojo/pom.xml    ./tutoring-pojo/
COPY tutoring-server/pom.xml  ./tutoring-server/

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests

# ==========================
# Runtime Stage
# ==========================
FROM openjdk:8-jre-slim
WORKDIR /app

COPY --from=builder /app/tutoring-server/target/tutoring-server-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]



