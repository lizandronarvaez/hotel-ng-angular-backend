# Etapa de construcción
FROM maven:3.8.6-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Etapa de ejecución
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=builder /target/hotel-ng-0.0.1-*.jar ./hotel-ng-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "hotel-ng-backend.jar"]