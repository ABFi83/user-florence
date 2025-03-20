# Fase di build
FROM maven:3.9.0-eclipse-temurin-21 AS build

# Copia il codice sorgente nel container
WORKDIR /app
COPY . .

# Compila il progetto
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim


WORKDIR /app

COPY target/user-api-0.0.1.jar user-api-0.0.1.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "user-api-0.0.1.jar"]
