package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.MovieDTO.GenreDTO;
import com.example.bookmyshow.models.Movie.Genre;
import com.example.bookmyshow.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional
    public List<Genre> updateGenres(List<GenreDTO> genreDTOs){
        try {
            //Fetch all genres
            List<Genre> existingGenres = (List<Genre>) genreRepository.findAll();

//        Existing genre in set for O(1) lookup
            Set<String> existingGenreNames = existingGenres.stream()
                    .map(Genre::getName)
                    .collect(Collectors.toSet());

            //Filter out the new genres
            List<Genre> newGenres = genreDTOs.stream()
                    .filter(genreDTO -> !existingGenreNames.contains(genreDTO.getName()))
                    .map(genreDTO -> {
                        Genre genre = new Genre();
                        genre.setName(genreDTO.getName());
                        return genre;
                    })
                    .toList();

            genreRepository.saveAll(newGenres);

            return newGenres;

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }
}
