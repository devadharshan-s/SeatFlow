package com.example.bookmyshow.movie.service;

import com.example.bookmyshow.movie.exception.GenreOperationException;
import com.example.bookmyshow.movie.model.Genre.Genre;
import com.example.bookmyshow.movie.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional
    public List<Genre> updateGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            throw new GenreOperationException("Genres list is empty");
        }

        // Normalize, trim and deduplicate while preserving input order.
        List<String> normalizedNames = genres.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        java.util.ArrayList::new
                ));

        if (normalizedNames.isEmpty()) {
            throw new GenreOperationException("Genres list is empty");
        }

        try {
            List<Genre> existingGenres = genreRepository.findByNameIn(normalizedNames);
            Set<String> existingNames = existingGenres.stream()
                    .map(Genre::getName)
                    .collect(java.util.stream.Collectors.toSet());

            List<Genre> newGenres = normalizedNames.stream()
                    .filter(name -> !existingNames.contains(name))
                    .map(name -> {
                        Genre genre = new Genre();
                        genre.setName(name);
                        return genre;
                    })
                    .toList();

            if (!newGenres.isEmpty()) {
                genreRepository.saveAll(newGenres);
            }

            return genreRepository.findByNameIn(normalizedNames);
        } catch (Exception e) {
            log.error("Failed to update genres", e);
            throw new GenreOperationException("Failed to update genres", e);
        }
    }
}
