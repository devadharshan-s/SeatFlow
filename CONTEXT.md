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

## Last Files Touched
- src/main/java/com/example/bookmyshow/movie/repository/MovieRepository.java
- src/main/java/com/example/bookmyshow/movie/repository/GenreRepository.java
- src/main/java/com/example/bookmyshow/movie/service/GenreService.java
- src/main/java/com/example/bookmyshow/movie/exception/GenreOperationException.java
- src/main/java/com/example/bookmyshow/GlobalExceptionHandler.java
