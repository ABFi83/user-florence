# Fase di build
FROM maven:3.9.0-eclipse-temurin-21 AS build

# Copia il codice sorgente nel container
WORKDIR /app
COPY . .

# Compila il progetto
RUN mvn clean install -DskipTests

# Fase di runtime
FROM openjdk:21-jdk-slim

# Imposta la cartella di lavoro all'interno del container
WORKDIR /app

# Copia solo il file .jar dal build stage
COPY --from=build /app/target/user-api-0.0.1.jar user-api-0.0.1.jar

# Espone la porta 8080
EXPOSE 8080

# Esegui l'applicazione
ENTRYPOINT ["java", "-jar", "user-api-0.0.1.jar"]
