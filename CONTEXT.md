# Context

## Goal
Refactor BookMyShowClone into a modular monolith. Currently refactoring movie module.

## Decisions
- Keep MovieCast as a join entity (single movie + single person).
- Genres can be created on the fly; cast members must already exist.
- Keep updateGenres; remove resolveGenres.

## Changes Done
- MovieRepository generic ID -> Long.
- Added GenreOperationException and global handler entries.
- GenreRepository: implemented findByNameIn.
- Removed resolveGenres from GenreService.

## Next Focus
- Refactor createMovie flow (genre resolve + cast validation + moviecast creation).
- Consider extracting MovieCastService.

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
