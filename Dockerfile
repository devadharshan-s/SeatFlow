# Multi-stage Dockerfile for SeatFlow microservices development
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy the parent pom and module poms to cache dependencies
COPY pom.xml .
COPY bookmyshow-eureka-server/pom.xml bookmyshow-eureka-server/
COPY bookmyshow-booking-service/pom.xml bookmyshow-booking-service/
COPY bookmyshow-payment-service/pom.xml bookmyshow-payment-service/
COPY bookmyshow-show-service/pom.xml bookmyshow-show-service/
COPY bookmyshow-theatre-service/pom.xml bookmyshow-theatre-service/
COPY bookmyshow-movie-service/pom.xml bookmyshow-movie-service/
COPY bookmyshow-user-service/pom.xml bookmyshow-user-service/

ARG MODULE_NAME

# Pre-fetch and cache Maven dependencies for the target module
RUN mvn dependency:go-offline -pl ${MODULE_NAME} -am

# Copy the source code of the module and package it
COPY ${MODULE_NAME}/src ${MODULE_NAME}/src
RUN mvn clean package -pl ${MODULE_NAME} -am -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG MODULE_NAME
COPY --from=builder /app/${MODULE_NAME}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
