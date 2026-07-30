# 02 — Evict showSeatsCache after a successful lock operation

**What to build:** After any seat lock succeeds (both the MySQL `LockService` path and the Redis
`SeatHoldService` path), the `showSeatsCache` Redis cache entry for that show must be evicted so the
next poll of `getShowSeats` returns live data. Without eviction, any subsequent call to
`GET /getShowSeats/{showId}` returns the stale cached list where the just-locked seats still show as
`AVAILABLE`, making locked seats invisible to all other windows.

`ShowSeatService.evictShowSeatsCache(showId)` already exists and works correctly — it is just not
called after lock operations.

**Blocked by:** 01 (so that a lock actually succeeds before we verify the eviction path).

**Status:** ready-for-agent

- [ ] In `ShowSeatController.lockSeats(...)`, after `lockService.lockSeats(...)` succeeds, resolve
      the `showId` for the locked seats (via `showSeatService.getShowIdForSeat(showSeatIds.get(0))`)
      and call `showSeatService.evictShowSeatsCache(showId)`.
- [ ] In `ShowSeatService.holdSeats(...)` (the Redis path), call `evictShowSeatsCache(showId)`
      after a successful hold. Derive `showId` from `getShowIdForSeat` on the first seat id before
      calling `seatHoldService.holdSeats(...)`.
- [ ] Verify end-to-end: lock seats from window A, then immediately call `GET /getShowSeats/{showId}`
      from a second terminal — the seats should now return with `status: "LOCKED"`.
