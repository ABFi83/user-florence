# User API - Spring Boot & Java 21

## Overview

This project is a RESTful API for user management built with **Spring Boot** and **Java 21**. It uses **PostgreSQL** as the database and is containerized with **Docker**.

## Prerequisites

Ensure you have the following installed on your system:

- `Java 21`
- `Maven`
- `Docker` & `Docker Compose`

## Setup and Execution

To build and run the application, follow these steps:

### 1. Build the application using Maven:

```sh
mvn clean install
```

### 2. Run the application using Docker Compose:

```sh
docker-compose up --build
```

## API Endpoints

Once the application is running, the API will be exposed at `http://localhost:8080`.

### Example Endpoints:

- `GET /api/users` - Retrieve a list of users
- `GET /api/users/{id}` - Retrieve a user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update an existing user
- `DELETE /api/users/{id}` - Delete a user
- `IMPORT /api/users/import` - Import users

## Swagger UI:
Once the application is running, you can access the Swagger UI to explore and test the API at:

http://localhost:8080/swagger-ui.html

## Environment Variables

You can configure the database connection and other settings via environment variables in the `docker-compose.yml` file.

## Stopping the Application

To stop the application, run:

```sh
docker-compose down
```

## Files
In the files folder, there are:
- test.csv: A CSV file for importing users.
- User API's.postman_collection.json: Postman collection for the API.
