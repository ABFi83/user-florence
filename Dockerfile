
FROM openjdk:17-jdk-slim


WORKDIR /app

COPY target/user-api-0.0.1.jar user-api-0.0.1.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "user-api-0.0.1.jar"]
