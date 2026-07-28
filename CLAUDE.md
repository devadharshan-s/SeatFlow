# Claude's Project Notes

## Current Task
**Complete.** Distributed rate limiting (Redisson `RRateLimiter`) and Resilience4j resilience (`@Retry`, `@CircuitBreaker`, `@TimeLimiter`) have been implemented and verified in `bookmyshow-booking-service`.

## Completed Tasks
1. Completed multi-module Maven project refactoring (unified parent POM, clean dependency versions).
2. Completed Netflix Eureka Service Discovery integration.
3. Completed Redis distributed seat locking in `bookmyshow-show-service`:
   - Redisson `RRateLimiter` + atomic Lua scripting in `SeatHoldService`.
   - Resilience4j `@CircuitBreaker` fallback to MySQL `LockService` in `ShowSeatService`.
   - Redis read-through caching via `@Cacheable` (`showSeatsCache`, `staticSeatDetails`).
4. Completed distributed rate limiting + resilient Feign wrappers in `bookmyshow-booking-service`:
   - `@RateLimit` custom annotation + `RateLimitAspect` (AOP, Redisson Token Bucket, per-user JWT or per-IP).
   - `ResilientSeatClient` delegate wrapper (`@Retry` with exponential backoff + `@CircuitBreaker`).
   - `GlobalExceptionHandler` extended for HTTP 429 and 503 responses.
   - Idempotent `bookSeats()` in `ShowSeatService` (safe for retry with same `ticketId`).

## Next Steps (Future Focus)
1. Keycloak / OAuth2 security configuration — JWT resource server, roles, and inter-service token propagation.
2. Monitoring stack — Spring Boot Actuator, Micrometer, Prometheus, Grafana.
3. Event-driven architecture — Kafka or RabbitMQ for async booking notifications and payment events.

## Architecture Decisions & Key Patterns (Interview Reference)

| Pattern | Where | Why |
|---|---|---|
| Lua atomic scripting | `SeatHoldService` | All-or-nothing multi-seat hold; no partial locks |
| Circuit Breaker fallback | `ShowSeatService` | MySQL as durable fallback when Redis is unavailable |
| Token Bucket rate limiting | `RateLimitAspect` | Allows bursts; distributed via Redis so all instances share counters |
| Decorator/Delegate Wrapper | `ResilientSeatClient` | Adds Resilience4j to Feign clients without modifying the interface (OCP) |
| Feign Fallback Bridge | `validateResponse()` | Converts Feign's soft error responses into thrown exceptions for Resilience4j |
| Idempotent bookSeats | `ShowSeatService` | Enables safe retries — same ticketId re-booking is a no-op |
| AOP cross-cutting concern | `RateLimitAspect` | Rate limiting decoupled from controller business logic |

## Findings

### Environment
- Services require `DB_PASSWORD` env var (default `@Shawn123`).
- Spring Boot pinned to `3.5.6` (matches local Maven cache at `C:\Users\devad\.m2\repository`).
- Redis: `redis-15621.crce263.ap-south-1-1.ec2.cloud.redislabs.com:15621`.
- Start Eureka first, then all services are startable from root with `mvn spring-boot:run -pl <module>`.

### bookmyshow-booking-service — Controllers
- `GET /selectSeats/{showId}?status=ALL` — @RateLimit 15/min
- `POST /bookTickets` — @RateLimit 3/min
- `DELETE /deleteBooking?ticketId={ticketId}` — @RateLimit 3/min

### bookmyshow-show-service — Key Endpoints
- `POST /show-seat/holdSeats?ticketId=&holdSeconds=` — Redisson Lua hold
- `POST /show-seat/confirmHold?ticketId=` — atomic Redis clear
- `POST /show-seat/releaseHold?ticketId=` — atomic Redis release
- `POST /lockSeats/{seconds}` — legacy MySQL fallback (used by CB)
- `POST /bookSeats/{ticketId}` — idempotent DB booking
