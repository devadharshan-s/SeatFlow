# 🌅 Morning Handoff & Testing Guide

This document summarizes the changes made to resolve frontend-to-backend connectivity and group shows by theatre. To keep the `main` branch pristine, all testing overrides and code changes have been pushed to a dedicated branch: **`frontend-integration-test`**.

---

## 📂 Git Branch Information
* **Test Branch:** `frontend-integration-test`
* **Remote Repo:** [https://github.com/devadharshan-s/SeatFlow](https://github.com/devadharshan-s/SeatFlow)
* **Status:** Staged, committed, and force-pushed successfully.

---

## 🛠️ Summary of Changes in `frontend-integration-test`

### 1. 🌐 Vite Reverse Proxy Resolution (Trailed Slashes)
* **Problem:** Direct page refreshes on `/movies` or `/shows` matched the Vite proxy rules (`'/movie'`, `'/show'`), routing page requests to the microservices (e.g., querying `/s` on backend) and returning 500 errors.
* **Fix:** Updated `vite.config.ts` proxy keys to require trailing slashes (`'/movie/'`, `'/show/'`). Refreshing `/movies` or `/shows` now falls back to Vite's `index.html` allowing React Router to mount client-side routes.

### 2. 🔐 Dev Test Security Bypasses (Spring Security & Feign)
* **Microservice Permissions:** Added temporary `SecurityConfig.java` to `movie`, `show`, `booking`, and `theatre` services allowing `.permitAll()` on all endpoints for local testing without active Keycloak tokens.
* **Ticket Service Fallback:** Patched `TicketService.java` to handle unauthenticated test requests dynamically, falling back to a default `userId = 1L` if no JWT exists in the security context.

### 3. 🛡️ Movie service Null-Safety Fix
* **Problem:** Manually inserting movies into the database without mapping genres/cast threw a `NullPointerException` when calling `.stream()` on uninitialized collections.
* **Fix:** Refactored `MovieService.java` to perform null checks, defaulting uninitialized genres or cast to empty lists.

### 🎨 4. Shows Grouped by Theatre Name
* **Action:** Redesigned `ShowListPage.tsx` to fetch theatres (`getAllTheatres()`) alongside shows in parallel. It maps theatre IDs to their actual names and groups the shows visually under distinct `TheatreSection` containers with styled headers and show counts.

### 🛠️ 5. Cache Configuration & Error Handler Fix (Show Service)
* **Problem:** The custom caching exception configuration threw a compilation error: `method does not override or implement a method from a supertype` because the custom class named the override method `cacheErrorHandler()` instead of `errorHandler()` from the `CachingConfigurer` interface.
* **Fix:** Corrected the signature to `errorHandler()` in [CacheExceptionConfig.java](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-show-service/src/main/java/org/example/bookmyshowshowservice/config/CacheExceptionConfig.java).

### 🚀 6. Seat Flow Payload & API Alignments
* **Problem 1 (Client payload mismatch):** The frontend was sending `{ showId, seatIds }` as an object to `/show-seat/lockSeats/{seconds}`, but the backend `ShowSeatController`'s `@PostMapping("/lockSeats/{seconds}")` expects a raw list of seat IDs (`List<Long>`). This caused a 400 Bad Request error.
* **Problem 2 (Missing ticketId in response):** `TicketDTO` on the backend lacked the `ticketId` field, meaning the frontend received `undefined` when attempting to build the payment client, leading to `NaN` being sent to `/create-intent`.
* **Problem 3 (Unlock body missing):** The frontend called `unlockSeats` with no request body on cancellation, while the backend expected `List<Long> showSeatIds`.
* **Fix:** 
  * Updated [show.ts](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/frontend/src/api/show.ts) to send seat IDs directly as a raw array (`seatIds.map(Number)`) to `/lockSeats/{seconds}` and pass `seatIds` to `/unlockSeats/{ticketId}`.
  * Added `ticketId`, `showSeatIds`, `userId`, and `amountPaid` to [TicketDTO.java](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-booking-service/src/main/java/org/example/bookmyshowbookingservice/booking/api/dto/TicketDTO.java).
  * Passed the locked seat IDs during unlock triggers in [PaymentStatusPage.tsx](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/frontend/src/pages/Payments/PaymentStatusPage.tsx).

### 🔌 7. Redis Connectivity & Cache Type Resolving
* **Problem 1 (Malformed Host):** The `spring.data.redis.host` configuration in both show and booking services default fallback values had duplicate ports appended (e.g. `host:port:port`), resulting in DNS lookup and initialization exceptions.
* **Problem 2 (Cache Resolution):** Spring Boot default cache type auto-configuration resolved to JCache (using Redisson) because both JCache and Redis were on the classpath. JCache threw 500 errors when attempting to access the un-pre-declared `'showSeatsCache'`.
* **Fix:** 
  * Corrected `spring.data.redis.host` in both `application.properties` files.
  * Explicitly configured `spring.cache.type=redis` in [application.properties](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-show-service/src/main/resources/application.properties) to force the Spring Boot `RedisCacheManager` to run, enabling dynamic cache creation.

### 🐳 8. Full Architecture Dockerization (`docker-integration-setup` branch)
* **Goal:** Wrap the entire microservices architecture in Docker to start everything (databases, IAM, microservices) with a single command and support hot-reloading on host source code changes.
* **Completed Configurations:**
  * **Unified Compose File:** Added [docker-compose.yml](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/docker-compose.yml) linking MySQL, Keycloak, and all 7 services.
  * **BuildKit Collision Fix:** Created 7 isolated Dockerfiles under [docker/Dockerfiles/](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/docker/Dockerfiles/) to prevent BuildKit build race conditions.
  * **Conflict-Free Database:** Mapped Docker MySQL to host port `3307` so you don't need to stop your host's local MySQL instance.
  * **Auto-Data Import (UTF-8 resolved):** Exported host databases into [backup.sql](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/docker/mysql/backup.sql) saved as UTF-8 (avoiding PowerShell's UTF-16 BOM null-byte importer errors) and mapped it to mount on startup.
  * **Keycloak Auto-Realm:** Set up [realm-export.json](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/docker/keycloak/realm-export.json) with predefined `bookmyshow-admin` permissions to allow dynamic registration.

---

## 🚀 How to Resume Testing in the Morning

Choose your preferred setup below to spin up and verify the entire integration flow:

### Option A: Clean Docker Setup (Recommended)
This runs the entire backend infrastructure and microservices with automatic watch/reload on save:

1. **Checkout the Docker branch:**
   ```bash
   git checkout docker-integration-setup
   ```
2. **Start the Docker Compose stack:**
   ```bash
   docker compose up -d
   ```
3. **Enable hot-reloading (in a separate terminal):**
   ```bash
   docker compose watch
   ```
4. **Run Vite frontend (on host):**
   ```bash
   cd frontend
   npm run dev
   ```

---

### Option B: Local Maven Run (No Docker)
If you prefer running services directly on the host using your local Maven compiler:

1. **Checkout the Test branch:**
   ```bash
   git checkout frontend-integration-test
   ```
2. **Start Eureka & Microservices (In Order):**
   * Eureka Server (Port `8761`): `mvn spring-boot:run -pl bookmyshow-eureka-server`
   * Theatre Service (Port `8087`): `mvn spring-boot:run -pl bookmyshow-theatre-service`
   * Movie Service (Port `8088`): `mvn spring-boot:run -pl bookmyshow-movie-service`
   * User Service (Port `8089`): `mvn spring-boot:run -pl bookmyshow-user-service`
   * Show Service (Port `8086`): `mvn spring-boot:run -pl bookmyshow-show-service`
   * Booking Service (Port `8085`): `mvn spring-boot:run -pl bookmyshow-booking-service`
   * Payment Service (Port `8084`): `mvn spring-boot:run -pl bookmyshow-payment-service`

3. **Start Vite Dev Server:**
   ```bash
   cd frontend
   npm run dev -- --force
   ```

---

### Step 4: Open Browser
Navigate to `http://localhost:5173/movies` (or port `5174` depending on local port occupancy). 
* Click a movie to view its details.
* View showtimes grouped under their actual **Theatre Names**.
* Select seats, reserve them, and complete a test checkout.
