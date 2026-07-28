# Developer & Agent Guidelines

## How to Run / Build

Since this is a microservices project, start services on the ports below. All services can be started from the **root project directory** without `cd`-ing into sub-folders:

```bash
mvn spring-boot:run -pl <module-name>
```

Start them in this order (Eureka must be first):

| Order | Module | Port |
|---|---|---|
| 1 | `bookmyshow-eureka-server` | `8761` |
| 2 | `bookmyshow-theatre-service` | `8087` |
| 3 | `bookmyshow-movie-service` | `8088` |
| 4 | `bookmyshow-user-service` | `8089` |
| 5 | `bookmyshow-show-service` | `8086` |
| 6 | `bookmyshow-booking-service` | `8085` |
| 7 | `bookmyshow-payment-service` | `8084` |

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
  * Redis distributed seat locking using Redisson & atomic Lua scripting with Resilience4j Circuit Breaker fallback to MySQL (`bookmyshow-show-service`).
  * Redis read-through caching for seat availability (`bookmyshow-show-service`).
  * Distributed rate limiting via Redisson `RRateLimiter` (Token Bucket, per-user/IP, AOP-driven) (`bookmyshow-booking-service`).
  * Resilient Feign client wrapping via `ResilientSeatClient` delegate pattern with `@Retry`, `@CircuitBreaker`, and `@TimeLimiter` (`bookmyshow-booking-service`).
  * Idempotent `bookSeats()` to enable safe retry semantics.
* **Current Focus:**
  * Keycloak / OAuth2 security configuration, inter-service token propagation.
* **Future Focus:**
  * Monitoring (Prometheus/Grafana), event-driven architecture (Kafka/RabbitMQ).

## Files / Folders to Avoid Touching

* All Service Folder & Files, as I will be writing those by myself. However you can pitch in by helping me plan it.

## Review / Deployment Rules

* Push every Git change to the repository: https://github.com/devadharshan-s/SeatFlow
