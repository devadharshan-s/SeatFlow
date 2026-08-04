# Booking Module Refactoring — Spec

## Problem Statement

The booking module (`bookmyshow-booking-service`) has accumulated several architectural debt items that blur service boundaries, pollute the database with premature records, and expose unnecessary inter-service coupling:

1. **`getSeats` is misplaced.** The `/selectSeats/{showId}` endpoint in the booking module is a pure pass-through to show-service's `ShowSeatClient`. It queries seat *availability* — a concern that belongs to the show domain, not the booking domain. This creates a shallow module: the booking service adds zero behaviour on top of the Feign call.

2. **Premature ticket creation pollutes the database.** `bookTicket()` calls `ticketRepository.save()` *before* seat hold/booking succeeds, creating orphaned rows on failure. The auto-generated `ticketId` is then used as the Redis hold key, tightly coupling the database identity to the transient locking lifecycle. The approved architecture in `handoff.md` already specifies a frontend-generated `bookingToken` (UUID) as the idempotency key — but the booking service hasn't adopted it yet.

3. **`validateTicketExists` has cross-service consumers.** Before removing it, we must audit its callers: `ShowSeatService` and `SeatHoldService` in show-service, `PaymentService` in payment-service, and the frontend's `booking.ts`. Simply deleting it will break multiple services.

4. **`bookSeats` still references stale `seatClient` directly.** Line 134 of `TicketService` still calls `seatClient.bookSeats(...)` instead of `resilientSeatClient.bookSeats(...)`, bypassing the Resilience4j decorator.

5. **Chatty HTTP round-trips.** Multiple sequential Feign calls (resolve → hold → book → confirm) each carry full HTTP handshake overhead. Batching seat operations would reduce latency.

## Solution

Refactor the booking module to enforce clean domain boundaries, defer ticket creation until payment confirmation, and use the `bookingToken` UUID as the idempotent correlation key throughout the booking lifecycle.

## User Stories

1. As a **mobile/web user**, I want to browse available seats for a show without the request routing through the booking service, so that seat availability loads faster with one fewer network hop.
2. As a **user selecting seats**, I want my seat hold to be tied to a `bookingToken` UUID (not a database-generated ticketId), so that no database row exists until my payment is confirmed.
3. As a **user retrying a failed booking**, I want the same `bookingToken` to produce the same result, so that I never get double-charged or duplicate tickets.
4. As a **developer on the show-service team**, I want `holdSeats`/`bookSeats` to accept a `bookingToken` instead of a `ticketId`, so that the show-service no longer needs to call back to booking-service to validate a ticket exists.
5. As a **developer on the payment-service team**, I want ticket creation to happen only after payment succeeds, so that `validateTicketExists` is always meaningful (no orphaned tickets).
6. As a **platform engineer**, I want batch seat operations to reduce the HTTP round-trip count during booking checkout, so that p99 latency drops.
7. As a **developer reviewing the codebase**, I want the booking module to contain only booking-domain logic (ticket lifecycle, payment orchestration), with no pass-through seat-availability proxying.

## Implementation Decisions

### 1. Move `getSeats` to show-service (Domain Ownership)

- The `/selectSeats/{showId}` endpoint moves from booking-service's `TicketController` to show-service's existing `ShowSeatController`. The booking module's `getSeats()` method, `ShowSeatClient` Feign interface, and `ShowSeatClientFallback` are all removed.
- The frontend's `selectSeats` call routes directly to show-service (already discoverable via Eureka gateway).
- **Seam impact:** Removes a shallow pass-through module from booking-service. The seam for seat availability is now entirely within show-service.

### 2. Replace `ticketId` with `bookingToken` in the Hold/Book Lifecycle

- The `TicketDTO` gains a `bookingToken` field (UUID, frontend-generated).
- `bookTicket()` no longer calls `ticketRepository.save()` upfront. Instead:
  1. Validate the show exists.
  2. Resolve seat IDs.
  3. Call `holdSeats(bookingToken, ...)` and `bookSeats(bookingToken, ...)` using the token.
  4. Return a response containing the `bookingToken` and held seat IDs — **no ticket row yet**.
- Ticket creation is deferred to a new `confirmBooking(bookingToken)` method, invoked after payment succeeds (by the payment webhook handler or a direct call from payment-service).
- **Redis key change:** `seat:hold:<showSeatId>` stores `bookingToken` instead of `ticketId`. The `SeatHoldService` already uses `bookingToken` for `seat:lock` — this aligns `seat:hold` to the same token.

### 3. Audit and Refactor `validateTicketExists`

| Caller | Current Usage | After Refactor |
|---|---|---|
| `ShowSeatService.validateTicketId()` | Feign call to booking `/validateTicket/{ticketId}` before every seat operation | Replace with local `bookingToken` null/format check — no cross-service call needed |
| `SeatHoldService.validateTicketId()` | Local null/range check only (no Feign call) | No change needed |
| `PaymentService.createPaymentIntent()` | Feign call to validate ticket before creating Stripe PaymentIntent | Remains — but now validates a *confirmed* ticket (post-payment), which will always exist. Alternatively, validate via `bookingToken` in Redis |
| Frontend `booking.ts` | Calls `/validateTicket/{ticketId}` | Remove this call — unnecessary once tickets are only created after payment |

> [!IMPORTANT]
> `validateTicketExists` **cannot be deleted immediately** — payment-service depends on it. The refactoring must be sequenced: first migrate show-service callers to `bookingToken`-based validation, then update payment-service, and only then remove the endpoint.

### 4. Fix stale `seatClient.bookSeats()` reference

- Line 134 of `TicketService` calls `seatClient.bookSeats(...)` directly instead of `resilientSeatClient.bookSeats(...)`. This bypasses retry and circuit breaker protection. Must be fixed as a prerequisite.

### 5. Batch Seat Lookup Endpoint

- Add a composite endpoint in show-service that combines resolve + hold into a single call: `POST /shows/{showId}/hold-seats` accepting `{ bookingToken, seatIds, holdSeconds }`.
- This eliminates one HTTP round-trip from the booking flow (resolve and hold happen atomically server-side).
- The booking-service's `SeatClient` and `ResilientSeatClient` gain a corresponding `holdAndResolveSeats(...)` method.

## Testing Decisions

- **Testing seam:** The primary seam to test is the `TicketService.bookTicket()` method. Tests should verify external behaviour: given a `bookingToken` + seat IDs + show ID, assert that the correct Feign calls are made in the right order and that no database row is created until `confirmBooking` is called.
- **Integration test for idempotency:** Send the same `bookingToken` twice and assert only one ticket/hold is created.
- **Regression test:** Ensure `cancelTicket` still works for tickets that were created via the new `confirmBooking` path.
- **No prior test art exists** in this project — these would be the first integration tests in the booking module.

## Out of Scope

- **Event-driven architecture (Kafka/RabbitMQ):** The `confirmBooking` flow is a synchronous Feign call for now. Event-driven ticket creation is a future milestone per AGENTS.md.
- **Frontend changes:** The frontend already sends `bookingToken` — wiring it through the new flow is deferred to a separate ticket.
- **Payment-service refactoring:** Beyond updating `validateTicketExists` callers, deeper payment-service changes are out of scope.
- **Monitoring/observability:** Prometheus/Grafana integration is a separate future milestone.

## Further Notes

- The `getJwt()` static utility should be refactored to return `Optional<Jwt>` as discussed earlier in this conversation. This is a minor cleanup bundled with the `bookTicket` refactor.
- The `TicketRepostiory` typo (missing 'o' in Repository) should be fixed if we're touching that file anyway.
- The `@Transactional` annotation was deliberately removed from `bookTicket()` previously to avoid a deadlock (see handoff.md Ticket 03). The new flow avoids this entirely by not writing to the database until `confirmBooking`.
