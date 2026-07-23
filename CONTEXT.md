# Context

## Goal
Integrate and spin up Redis as a cache for the seat locking mechanism to replace slow service-layer and database-layer locks.

## Next Focus
- Configure Redis connection properties in `bookmyshow-show-service`.
- Implement distributed locking mechanism using Redis (SETNX or Redisson client).
- Ensure thread-safe seat selection operations in the code.
- Establish graceful TTL seat release policies.

## Runtime Notes
- Verified `bookmyshow-theatre-service` starts successfully on port `8087` when `DB_PASSWORD=@Shawn123`.
- The failure seen during startup was not application logic; it was MySQL authentication:
  - `Access denied for user 'root'@'localhost' (using password: YES)`
- Maven resolution in the sandbox was also misleading because the default local repo was `C:\Users\CodexSandboxOffline\.m2`, while the populated cache is under `C:\Users\devad\.m2\repository`.
- The project is pinned to Spring Boot `3.5.6`, which matches the cached local Maven artifacts and allows offline startup.

## Last Files Touched
- src/main/java/com/example/bookmyshow/movie/repository/MovieRepository.java
- src/main/java/com/example/bookmyshow/movie/repository/GenreRepository.java
- src/main/java/com/example/bookmyshow/movie/service/GenreService.java
- src/main/java/com/example/bookmyshow/movie/exception/GenreOperationException.java
- src/main/java/com/example/bookmyshow/GlobalExceptionHandler.java
