# Etapa de ejecución
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY target/hotel-ng-*.jar ./hotel-ng-backend.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "hotel-ng-backend.jar"]