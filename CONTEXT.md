# Context

## Completed Milestones

1. **Maven Multi-Module POM Refactoring** — Unified parent POM, shared properties, and centralized dependency management across all 7 services.
2. **Netflix Eureka Service Discovery** — All services register with `bookmyshow-eureka-server` (port `8761`) and resolve each other by service name via Feign.
3. **Redis Distributed Seat Locking (`bookmyshow-show-service`):**
   - `SeatHoldService` using Redisson atomic Lua scripts for all-or-nothing multi-seat holds.
   - `ShowSeatService` delegates hold/release/confirm to `SeatHoldService` with `@CircuitBreaker` fallback to MySQL `LockService`.
   - Read-through caching via `@Cacheable` (`showSeatsCache`, `staticSeatDetails`) backed by Redis.
4. **Distributed Rate Limiting + Resilient Feign Calls (`bookmyshow-booking-service`):**
   - `@RateLimit` annotation + `RateLimitAspect` using Redisson `RRateLimiter` (Token Bucket, per-user email or per-IP fallback).
   - `ResilientSeatClient` delegate wrapper adding `@Retry` (exponential backoff) + `@CircuitBreaker` to all downstream Feign calls.
   - Feign fallback ↔ Resilience4j exception bridge via `validateResponse()` status code inspection.
   - Idempotent `bookSeats()` in `ShowSeatService` — safe to retry with the same `ticketId`.

## Goal & Next Focus

- **Keycloak / OAuth2 security configuration** — JWT resource server setup, roles, and inter-service token propagation.
- **Monitoring** — Spring Boot Actuator, Micrometer metrics, Prometheus scraping, Grafana dashboards.
- **Event-Driven Architecture** — Kafka or RabbitMQ for booking confirmation notifications and payment event publishing.

## Runtime Notes

- All services use `DB_PASSWORD` env var (default: `@Shawn123`) for MySQL auth.
- Spring Boot is pinned to `3.5.6` to match the locally cached Maven artifacts.
- Redis is hosted on Redis Labs cloud (`redis-15621.crce263.ap-south-1-1.ec2.cloud.redislabs.com:15621`).
- Start Eureka first (`-pl bookmyshow-eureka-server`), then all other services can be started from the root in parallel using `mvn spring-boot:run -pl <module>`.

## Key Redis Key Namespaces

| Namespace | Format | Owned By |
|---|---|---|
| Seat hold | `seat:hold:<showSeatId>` | `bookmyshow-show-service` |
| Ticket hold tracker | `ticket:hold:<ticketId>` | `bookmyshow-show-service` |
| Seat availability cache | `showSeatsCache::<showId>-<STATUS>` | `bookmyshow-show-service` |
| Rate limiting | `ratelimit:<methodName>:<email/ip>` | `bookmyshow-booking-service` |
