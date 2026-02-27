package com.example.bookmyshow.movie.service;

import com.example.bookmyshow.exception.MovieNotFoundException;
import com.example.bookmyshow.movie.api.dto.MovieCastDTO;
import com.example.bookmyshow.movie.api.dto.MovieDTO;
import com.example.bookmyshow.movie.api.dto.MovieResponseDTO;
import com.example.bookmyshow.movie.api.dto.MovieUpdateDTO;
import com.example.bookmyshow.movie.api.dto.person.PersonDTO;
import com.example.bookmyshow.movie.model.Genre.Genre;
import com.example.bookmyshow.movie.model.Movie;
import com.example.bookmyshow.movie.model.MovieCast;
import com.example.bookmyshow.movie.model.Person;
import com.example.bookmyshow.movie.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final MovieCastService movieCastService;
    private final ModelMapper modelMapper;
    

    @Transactional
    public List<MovieDTO> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        Page<Movie> movies = movieRepository.findAll(pageable);

        if(movies.isEmpty()){
            throw new MovieNotFoundException("Movies not found!");
        }

        return movies.stream().map(
                movie -> {
                    MovieDTO dto = modelMapper.map(movie, MovieDTO.class);
                    dto.setGenres(movie.getGenres().stream().map(Genre::getName).toList());
                    return dto;
                }
        ).toList();
    }

    @Transactional
    public MovieResponseDTO getMovie(long id) {
          Movie movie = movieRepository.findBymovieId(id)
                  .orElseThrow(() -> new MovieNotFoundException("Movie Not found, Please check Movie Id: " + id));

          MovieResponseDTO response = modelMapper.map(movie, MovieResponseDTO.class);
          response.setGenres(movie.getGenres().stream().map(Genre::getName).toList());
          response.setCast(movie.getCast().stream().map(
                  movieCast -> {
                      MovieCastDTO dto = new MovieCastDTO();
                      Person person = movieCast.getPerson();
                      PersonDTO personDTO = new PersonDTO();
                      personDTO.setPersonId(person.getPersonId());
                      dto.setPerson(personDTO);
                      return dto;
                  }
          ).toList());

          return response;
    }

    @Transactional
    public MovieResponseDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setRuntime(movieDTO.getRuntime());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setCBFC(movieDTO.getCBFC());

        movie = movieRepository.save(movie);

        List<Genre> finalGenres = genreService.updateGenres(movieDTO.getGenres());

        movie.setGenres(finalGenres);

        List<MovieCast> finalCast = movieCastService.resolvePersons(movie, movieDTO.getCast());

        movie.setCast(finalCast);

        movieRepository.save(movie);
        
        return modelMapper.map(movie, MovieResponseDTO.class);
    }

    @Transactional
    public MovieUpdateDTO updateMovie(long movieId, MovieUpdateDTO movieDTO) {

            Movie movie = movieRepository.findBymovieId(movieId)
                    .orElseThrow(() -> new MovieNotFoundException("Movie not found! for the id: " + movieId));

            if(movieDTO.getTitle() != null) movie.setTitle(movieDTO.getTitle());
            if(movieDTO.getRuntime() != null) movie.setRuntime(movieDTO.getRuntime());
            if(movieDTO.getLanguage() != null) movie.setLanguage(movieDTO.getLanguage());
            if(movieDTO.getCBFC() != null) movie.setCBFC(movieDTO.getCBFC());
            if(movieDTO.getRating() != null) movie.setRating(movieDTO.getRating());
            if(movieDTO.getGenres() != null) {
                List<Genre> newGenres = genreService.updateGenres(movieDTO.getGenres());
                List<Genre> allGenres = new ArrayList<>(movie.getGenres());
                allGenres.addAll(newGenres);
                movie.setGenres(allGenres);
            }

            movie = movieRepository.save(movie);

            return movieDTO;
        }

    @Transactional
    public void deleteMovie(long movieId){

        Movie movie = movieRepository.findBymovieId(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found! for the id: " + movieId));
        movieRepository.delete(movie);
    }
}




