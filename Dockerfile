
FROM openjdk:17-jdk-slim


WORKDIR /app

COPY target/myapp.jar user-api-0.0.1.jar


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "user-api-0.0.1.jar"]
