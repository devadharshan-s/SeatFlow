# 01 — Translate seatId → showSeatId before calling lockSeats

**What to build:** When a user clicks "Lock Seats" in `ShowBookingPage`, the UI must first resolve
the selected `seatId` values (physical seat IDs from the theatre service) into `showSeatId` values
(the join-table IDs used by the show service) by calling `POST /shows/{showId}/resolve-seat-ids`,
then pass the resolved IDs to `POST /show-seat/lockSeats/{seconds}`.

Currently `lockSeats()` in `frontend/src/api/show.ts` sends the raw `seatId` numbers straight to the
lock endpoint. The lock endpoint calls `LockService.lockSeats(showSeatIds, seconds)`, which looks up
rows by `showSeatId` — so the lookup finds nothing and the lock silently fails or throws a 409.

**Blocked by:** None — can start immediately.

**Status:** ready-for-agent

- [ ] Add `resolveShowSeatIds(showId, seatIds)` function in `frontend/src/api/show.ts` that calls
      `POST /show-seat/shows/{showId}/resolve-seat-ids` with the array of `seatId` numbers and returns
      the array of `showSeatId` numbers from `response.data.data`.
- [ ] In `ShowBookingPage.tsx` `handleLockSeats`, call `resolveShowSeatIds` first, then pass the
      resolved IDs to `lockSeats`. Surface a user-friendly error if resolution fails.
- [ ] Verify that after locking, `getShowSeats` for that show returns those seats with
      `status: "LOCKED"` (can be checked via `curl` against port 8086).
- [ ] Remove the debug `ex.printStackTrace()` / `System.out.println` lines added to
      `GlobalExceptionHandler.java` — they served their purpose.
