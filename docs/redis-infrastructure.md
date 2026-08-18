# Redis Infrastructure & Distributed Patterns

This document details the shared Redis database infrastructure, its role as a distributed coordinator, custom Lua script specifications, rate-limiting rules, caching, and connection fallback proxying.

---

## 1. Role in System

SeatFlow uses a single hosted **Redis Labs Cloud** instance as a highly performant distributed coordinator. Redis handles:
* **Distributed Atomic Locks**: Locking individual seats for a temporary period (5 minutes) while payment completes.
* **Distributed Rate Limiting**: Throttling incoming client bookings and seat requests across horizontally-scaled API instances.
* **Distributed Caching**: Lowering database read pressure by caching seat layouts and show info.

---

## 2. Redisson Client Config & Fallback Proxy

### Config Coordinates:
* Located in: `bookmyshow-show-service` & `bookmyshow-booking-service`
* Managed by: [RedissonConfig](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-show-service/src/main/java/org/example/bookmyshowshowservice/config/RedissonConfig.java)

### Interview-Ready Pattern: Startup Resilience (Dynamic Proxy)
If the remote Redis instance is unavailable at service startup, the Redisson connection would ordinarily crash the entire Spring Application Context. To prevent this:
1. The `redissonClient()` bean catches connection failures.
2. If connection fails, it generates a JDK **Dynamic Proxy** implementation of `RedissonClient`:
   ```java
   Proxy.newProxyInstance(..., (proxy, method, args) -> {
       throw new RedisConnectionException("Redis server is unreachable...");
   });
   ```
3. When the service executes any subsequent lock or rate-limit request, the proxy throws `RedisConnectionException`, which is caught by Resilience4j’s `@CircuitBreaker` annotation, falling back safely to relational MySQL storage behaviors.

---

## 3. Distributed Seat Locking (Lua Scripts)

To reserve tickets safely, the application locks multiple seats concurrently. This requires *all-or-nothing* transactional semantics: either all seats are locked, or none are. Since standard Redis lock patterns (like `RLock`) would require acquiring multiple locks sequentially (introducing deadlock hazards), SeatFlow implements seat holds via **atomic Lua scripting**.

### A. Hold Seats Script
* **Redis Key Structure**:
  * Seat hold key: `seat:hold:<showSeatId>` (Value: `ticketId` owner string)
  * Ticket hold key: `ticket:hold:<ticketId>` (Value: comma-separated seat IDs)
* **Lua Logic**:
  ```lua
  -- Step 1: Check if any of the requested seats are already held
  for i, key in ipairs(KEYS) do
      if redis.call('exists', key) == 1 then
          return 0
      end
  end
  -- Step 2: Set holds on all seats with the specified TTL (PX/milliseconds)
  local ttl = ARGV[2]
  local owner = ARGV[1]
  for i, key in ipairs(KEYS) do
      redis.call('set', key, owner, 'PX', ttl)
  end
  -- Step 3: Register the ticket tracker mapping to track these seats
  redis.call('set', ARGV[3], ARGV[4], 'PX', ttl)
  return 1
  ```
* **Why Lua?** Redis processes scripts in a single-threaded execution context, guaranteeing that the `exists` checks and `set` commands run as a single atomic operation. No other client can interleave requests between the checks and the locks.

### B. Release/Confirm Holds Script
* **Lua Logic**:
  ```lua
  -- Step 1: Retrieve list of seat IDs associated with this ticket
  local joined = redis.call('get', KEYS[1])
  if not joined then
      return 0
  end
  local seatKeyPrefix = ARGV[2]
  local owner = ARGV[1]
  -- Step 2: Loop and release each seat only if currently owned by this ticket
  for seatId in string.gmatch(joined, '([^,]+)') do
      local seatKey = seatKeyPrefix .. seatId
      if redis.call('get', seatKey) == owner then
          redis.call('del', seatKey)
      end
  end
  -- Step 3: Delete ticket hold tracker
  redis.call('del', KEYS[1])
  return 1
  ```
* **Protection against Overlapping TTL Expirations**: When a seat hold expires, another user may quickly hold it. If the first user later triggers a release/cancel, checking `if redis.call('get', seatKey) == owner` prevents the first user from deleting the new user's lock.

---

## 4. Distributed Rate Limiting (`RRateLimiter`)

The booking service prevents bot abuse using token bucket rate limiters configured per API endpoint:
* **Key Generation**: `ratelimit:<methodName>:<email/ip>`
  * Uses the client's Keycloak JWT email claim as the token key.
  * If unauthenticated, falls back to the remote client IP extracted via `X-Forwarded-For` HTTP header.
* **Mechanism**:
  1. Grabs `redissonClient.getRateLimiter(rateLimiterKey)`.
  2. Sets a Token Bucket window config using `trySetRate(RateType.PER_CLIENT, rateLimit.rate(), window)`.
  3. Invokes `limiter.tryAcquire()`. Returns `429 Too Many Requests` on failure.

---

## 5. Read-Through Caching

Caches are managed in the `ShowSeatService` utilizing Spring's `@Cacheable` abstraction backed by Redis serialization:
* **Caches**:
  1. `staticSeatDetails` (Key: `#showId`): Caches structural layout of seats retrieved from the Theatre Service.
  2. `showSeatsCache` (Key: `#showId + '-' + #status`): Caches seat availability status representation.
* **Eviction**: Programmatically evicted using `cacheManager.getCache("showSeatsCache").evict(...)` during any status-altering booking, cancel, or hold operation.

---

## 6. Reputable Reference Sources

* **Redis Lua Scripting and Atomicity**: [Redis Official Documentation on Programmability](https://redis.io/docs/manual/programmability/)
* **Redisson Distributed Locks & Scripts**: [Redisson Wiki: Distributed Locks](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers)
* **Redisson RRateLimiter Rate Limiting**: [Redisson Wiki: Rate Limiter](https://github.com/redisson/redisson/wiki/14.-Integration-with-frameworks#143-rate-limiting)
* **Spring Boot Cache with Redis**: [Baeldung Guide to Spring Cache](https://www.baeldung.com/spring-cache-tutorial)
* **JDK Dynamic Proxies**: [Baeldung Guide to Java Dynamic Proxies](https://www.baeldung.com/java-dynamic-proxies)
