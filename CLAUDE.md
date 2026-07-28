# Claude's Project Notes

## Current Task
Integrate distributed rate limiting using Redisson (`RRateLimiter`) and downstream network call resilience using Resilience4j (`@Retry`, `@CircuitBreaker`, `@TimeLimiter`) in `bookmyshow-booking-service`.

## Completed Tasks
- Completed integration of Netflix Eureka Service Discovery.
- Completed multi-module Maven project refactoring (unified parent POM, clean dependency versions).
- Completed Redis-based distributed seat locking mechanism using Redisson and atomic Lua scripting, with Resilience4j Circuit Breaker fallback to MySQL database-layer locks.
- Completed Redis-based caching for real-time seat availability checks (`showSeatsCache` and `staticSeatDetails` cache).

## Approach / Next Steps
1. Make `bookSeats` idempotent in `ShowSeatService` to support safe retries.
2. Add Redis, Redisson, Resilience4j, and AOP dependencies to `bookmyshow-booking-service`'s `pom.xml`.
3. Configure Redis properties and Resilience4j Retry, Circuit Breaker, and TimeLimiter in `application.properties` of the booking service.
4. Implement AOP-driven `@RateLimit` annotation and aspect using Redisson's `RRateLimiter` (namespaced by JWT user email or IP).
5. Build the delegate-wrapper `ResilientSeatClient` to add retry/circuit-breaker/timeout capabilities to `SeatClient` Feign calls.
6. Swap dependency in `TicketService` to use `ResilientSeatClient`.


## Findings
- **Startup / environment check**:
    - `bookmyshow-theatre-service` boots successfully on port `8087` when `DB_PASSWORD=@Shawn123`.
    - The actual startup failure was MySQL auth, not Spring wiring: `Access denied for user 'root'@'localhost' (using password: YES)`.
    - Maven in this sandbox initially resolved against `C:\Users\CodexSandboxOffline\.m2`, which caused a false parent-POM/plugin resolution failure. The populated cache is under `C:\Users\devad\.m2\repository`.
    - Spring Boot `3.5.6` is the compatible version for the cached local artifacts and is the version the services should stay on for offline launches here.

- **bookmyshow-booking-service**:
    - Uses `spring-boot-starter-data-jpa` and `mysql-connector-j`. Candidate for HikariCP integration.
    - **DTOs:**
        - `TicketDTO`: `showId` (long), `seatIds` (List<Long>) - Used for booking request.
        - `TicketResponseDTO`: `ticketId`, `showSeatIds`, `showId`, `userId`, `amountPaid` - Used for booking response.
        - `SeatAvailabilityResponse`: `showSeatId`, `seatId`, `status` - Used for displaying seat availability.
    - **Controllers (`TicketController`):**
        - `GET /selectSeats/{showId}?status=ALL`: Get seat availability.
        - `POST /bookTickets`: Book tickets (consumes `TicketDTO`). Expected response to be `TicketResponseDTO`.
        - `DELETE /deleteBooking?ticketId={ticketId}`: Cancel a ticket.
        - `GET /validateTicket/{ticketId}`: Validate ticket existence.
        - `DELETE /getTickets/{showId}`: Delete tickets by show ID (likely administrative).

- **bookmyshow-movie-service**:
    - **DTOs:**
        - `MovieDTO`: `title`, `genres` (List<String>), `runtime`, `language`, `CBFC`, `cast` (List<MovieCastDTO>) - Used for creating/updating movies.
        - `MovieResponseDTO`: `movieId`, `title`, `genres` (List<String>), `runtime`, `language`, `CBFC`, `cast` (List<MovieCastDTO>) - Used for retrieving movie details.
        - `MovieUpdateDTO`: `title`, `genres` (List<String>), `rating` (Double), `runtime` (Integer), `language`, `CBFC`, `cast` (List<MovieCastDTO>), `releaseDate` (java.sql.Date) - Used for updating movie details.
        - `MovieCastDTO`: `person` (PersonDTO).
        - `PersonDTO` (minimal, only `personId`): `personId` (Long).
        - `PersonResponseDTO`: `personId` (Long), `name` (String), `age` (int) - Full person details.
        - `PersonCreateDTO`: `name` (String), `age` (Integer) - Used for creating a person.
        - `PersonUpdateDTO`: `name` (String), `age` (Integer) - Used for updating a person.
        - `PersonMoviesUpdateDTO`: `movieIds` (List<Long>) - Used for updating movies associated with a person.
    - **Controllers (`MovieController`):**
        - `GET /getAllMovies?page=0&size=10`: Get paginated list of movies.
        - `GET /getMovie/{movieId}`: Get details for a specific movie.
        - `POST /createMovie`: Create a new movie (consumes `MovieDTO`).
        - `PATCH /updateMovie/{movieId}`: Update an existing movie (consumes `MovieUpdateDTO`).
        - `DELETE /deleteMovie/{movieId}`: Delete a movie.
    - **Controllers (`PersonController`):**
        - `GET /getPerson/{personId}`: Get details for a specific person.
        - `GET /getAllPersons?page=0&size=10`: Get paginated list of persons.
        - `POST /createPerson`: Create a new person (consumes `PersonCreateDTO`).
        - `PATCH /updatePerson/{personId}`: Update an existing person (consumes `PersonUpdateDTO`).
        - `PATCH /person/{personId}/movies`: Update movies associated with a person (consumes `PersonMoviesUpdateDTO`).
        - `DELETE /deletePerson/{personId}`: Delete a person.

- **bookmyshow-payment-service**:
    - Uses `spring-boot-starter-data-jpa` and `mysql-connector-j`. Candidate for HikariCP integration.
    - **DTOs:**
        - `CreatePaymentRequest`: `ticketId` (Long), `amount` (BigDecimal), `currency` (String) - Used for initiating a payment.
        - `PaymentResponse`: `paymentId` (Long), `ticketId` (Long), `amount` (BigDecimal), `currency` (String), `status` (PaymentStatus), `stripePaymentIntentId` (String), `clientSecret` (String), `returnUrl` (String) - Used for payment intent response and Stripe integration.
    - **Controllers (`PaymentController`):**
        - `POST /payments/intent`: Create a payment intent (consumes `CreatePaymentRequest`, returns `PaymentResponse`).
        - `GET /payments/return`: Payment redirect URL.

- **bookmyshow-show-service**:
    - Uses `spring-boot-starter-data-jpa`, `mysql-connector-j`, and `spring-boot-starter-data-redis`. Candidate for HikariCP integration for MySQL.
    - **DTOs:**
        - `CreateShowRequestDTO`: `showId` (int), `theatreId` (long), `screenId` (int), `startTime` (LocalDateTime), `ticketPrice` (double) - Used for creating a show.
        - `SeatAvailabilityResponse`: `seatId` (long), `rowNumber` (String), `seatNumber` (int), `status` (String), `booked` (boolean), `lockedUntil` (LocalDateTime), `price` (int), `category` (String) - Detailed seat information.
        - `ShowDTO`: `theatreId` (long), `screenId` (long), `movieId` (long), `startTime` (LocalDateTime), `endTime` (LocalDateTime) - Basic show details.
        - `ShowResponseDTO`: `showId` (long), `theatreId` (long), `screenId` (long), `movieId` (long), `startTime` (LocalDateTime), `endTime` (LocalDateTime) - Show details with ID.
    - **Controllers (`ShowsController`):**
        - `GET /getAllShows?page=0&size=10`: Get paginated list of shows.
        - `GET /getShowById?showId={showId}`: Get details for a specific show.
        - `POST /createShow`: Create a new show (consumes `ShowDTO`).
        - `PATCH /updateShow/{showId}`: Update an existing show (consumes `ShowDTO`).
        - `DELETE /deleteShow?showId={showId}`: Delete a show.
    - **Controllers (`ShowSeatController`):**
        - `GET /getShowSeats/{showId}?status=ALL`: Get detailed seat availability for a show.
        - `POST /lockSeats/{seconds}`: Lock seats for a duration.
        - `POST /bookSeats/{ticketId}`: Book seats for a ticket.
        - `POST /unlockSeats/{ticketId}`: Unlock seats associated with a ticket.
        - `DELETE /cancelSeats/{ticketId}`: Cancel seats associated with a ticket.
        - `POST /shows/{showId}/resolve-seat-ids`: Resolve show seat IDs (internal/utility).
        - `GET /getShowSeatsByTicket/{ticketId}`: Get show seats by ticket ID.
