# Context

## Completed Achievements
- Completed integration of Netflix Eureka Service Discovery.
- Completed Maven multi-module POM refactoring (unified parent configuration, shared properties, and dependency management).

## Goal & Next Focus
- Integrate, wire up, and upgrade Redis-based distributed seat locking mechanism (`SeatHoldService`) to replace MySQL-based database-layer locks (`LockService`).
- Upgrade Redis locking mechanism to Redisson/Lua scripts for atomic multi-seat operations.
- Introduce resilience / fallback mechanisms (Resilience4j) for Redis.
- Implement Redis caching for heavy reads (show/seat availability).

## Runtime Notes


## Last Files Touched

