FROM maven:3.9.9-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -Dmaven.test.skip=true

FROM amazoncorretto:21-alpine-jdk	
WORKDIR /app
COPY --from=builder /app/target/hotel-ng-*.jar ./hotel-ng-backend.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "hotel-ng-backend.jar"]
