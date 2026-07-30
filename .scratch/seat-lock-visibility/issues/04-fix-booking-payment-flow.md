# 04 — Fix booking and payment flow end-to-end

**What to build:** `PaymentStatusPage` fires `bookTickets()` on mount and then `createPaymentIntent()`.
Both calls fail today for three compounding reasons:

1. **Wrong payload shape:** `booking.ts` sends `{ showId: string, seatIds: string[], userId: string }`
   but `TicketDTO` in `booking-service` expects `{ showId: long, seatIds: List<Long>, userId: Long }`.
   The frontend must send numeric types and must also resolve `seatId → showSeatId` before booking
   (the booking service resolves it internally, but still requires `seatIds` to be the correct seat
   IDs that were locked — which are `showSeatId` values after ticket 01 is done).

2. **Missing JWT in booking request:** `booking-service` is protected by Spring Security OAuth2
   resource server — every request must carry `Authorization: Bearer <jwt>`. The `bookingApi` Axios
   instance has no interceptor that attaches the token stored in `localStorage['jwt_token']`.
   Same issue applies to `paymentApi`.

3. **`userId` type mismatch:** `PaymentStatusPage` derives `userId` as `user?.email ?? 'guest'`
   (a string) and passes it to `bookTickets(..., userId)`. The `booking-service` ignores the passed
   `userId` field (it resolves it from the JWT `email` claim internally), so this is harmless only
   if the JWT is present. Without the JWT, the service falls back to `userId=1L` — which silently
   succeeds but attaches the booking to the wrong user.

**Blocked by:** 01 (so that `lockedSeatIds` in the navigation state are `showSeatId` values, which
booking-service's internal `resolveShowSeatIds` call expects).

**Status:** ready-for-agent

- [ ] Add a shared Axios request interceptor (or a helper `getAuthHeaders()`) in
      `frontend/src/api/` that reads the JWT from `localStorage['jwt_token']` and appends
      `Authorization: Bearer <token>` to every outgoing request. Apply this interceptor to
      `bookingApi` and `paymentApi` Axios instances.
- [ ] In `booking.ts`'s `bookTickets`, send `seatIds` as `seatIds.map(Number)` (not `.map(String)`)
      so numeric seat IDs are sent as JSON numbers, not strings.
- [ ] In `PaymentStatusPage.tsx`, log (or surface in the error UI) the raw `err.response?.data`
      body for both booking and payment errors so failures are diagnosable without opening DevTools.
- [ ] Verify: after locking seats, navigate to `PaymentStatusPage` — booking succeeds and a Stripe
      payment intent `clientSecret` is returned. The page reaches the `success` stage.
- [ ] Remove the temp debug `ex.printStackTrace()` / `System.out.println` lines from
      `GlobalExceptionHandler.java` in `bookmyshow-show-service` (they were added during debugging
      and are not needed long-term).
