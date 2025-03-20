
FROM maven:3.9.0-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim


WORKDIR /app


COPY --from=build /app/target/user-api-0.0.1.jar user-api-0.0.1.jar


EXPOSE 8080

# Esegui l'applicazione
ENTRYPOINT ["java", "-jar", "user-api-0.0.1.jar"]
