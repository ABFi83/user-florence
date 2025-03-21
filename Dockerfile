FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/user-api-0.0.1.jar /app/user-api-0.0.1.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "/app/user-api-0.0.1.jar"]
