# Etapa de construcción
FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.compiler.release=21

# Etapa de ejecución
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=builder /app/target/hotel-ng-*.jar ./hotel-ng-backend.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "hotel-ng-backend.jar"]