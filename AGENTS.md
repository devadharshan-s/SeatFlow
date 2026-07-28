# Developer & Agent Guidelines

## How to Run / Build

Since this is a microservices project, run these services on the mentioned ports. Please check if these ports are already in use and terminate existing non-vital processes if needed:
* **bookmyshow-user-service:** Port `8089`
* **bookmyshow-movie-service:** Port `8088`
* **bookmyshow-theatre-service:** Port `8087`
* **bookmyshow-show-service:** Port `8086`
* **bookmyshow-booking-service:** Port `8085`
* **bookmyshow-payment-service:** Port `8084`
* **bookmyshow-eureka-server:** Port `8761`

## How to Test

We currently do not have test classes. Please verify that the expected output matches the specified schemas/DTOs and that no exceptions are thrown.

## Coding Conventions

* **Spacing:** Leave one blank line at the start and end of every method body.
* **Comments:** Write comments only when necessary. For example:
  * Calling other microservices/external APIs.
  * Complex or tricky logic implementations.
  * Crucial optimizations or workarounds.
  * Avoid commenting on standard CRUD operations.

## Architecture Notes

* **Resume & Interview Ready:** This project highlights microservice architecture best practices. Explain all modifications in interview-level depth and highlight key patterns.
* **Completed Milestones:**
  * Unified Maven parent POM refactoring.
  * Netflix Eureka Service Discovery integration.
  * Redis distributed seat locking using Redisson & atomic Lua scripting with Resilience4j Circuit Breaker fallback to MySQL.
* **Current Focus:**
  * Integrating Redisson-based distributed rate limiting (`RRateLimiter`) and Resilience4j Retry / TimeLimiter.
* **Future Focus:**
  * Keycloak/OAuth2 configuration, monitoring (Prometheus/Grafana), event-driven architecture (Kafka/RabbitMQ).

## Files / Folders to Avoid Touching

* None currently.

## Review / Deployment Rules

* Push every Git change to the repository: https://github.com/devadharshan-s/SeatFlow
