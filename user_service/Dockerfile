FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY ../.. .

RUN mvn clean package -pl user_service -am -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
VOLUME /tmp
COPY --from=builder /build/user_service/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]