package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.MovieDTO.*;
import com.example.bookmyshow.DTO.MovieDTO.PersonDTO.PersonDTO;
import com.example.bookmyshow.models.Movie.Genre;
import com.example.bookmyshow.models.Movie.Movie;
import com.example.bookmyshow.models.Movie.MovieCast;
import com.example.bookmyshow.models.Person;
import com.example.bookmyshow.models.Theatre.Shows;
import com.example.bookmyshow.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final ShowsRepository showsRepository;
    private final GenreRepository genreRepository;
    private final PersonRepository personRepository;
    private final MovieCastRepository movieCastRepository;
    private final GenreService genreService;
    private final ModelMapper modelMapper;

    @Transactional
    public ResponseEntity<ApiResponse<List<MovieResponseDTO>>> getAllMovies() {
        try {

            List<Shows> shows = (List<Shows>) showsRepository.findAll();

            Map<Movie, Set<Long>> grouped = shows.stream()
                    .collect(Collectors.groupingBy(
                            Shows::getMovie,
                            Collectors.mapping(
                                    show -> show.getTheatre().getTheatreId(),
                                    Collectors.toSet()
                            )
                    ));

            List<MovieResponseDTO> movieResponseDTOS = new ArrayList<>();

            for (Movie movie : grouped.keySet()) {
                MovieResponseDTO movieResponseDTO = new MovieResponseDTO();
                movieResponseDTO.setMovieId(movie.getMovieId());
                movieResponseDTO.setTitle(movie.getTitle());
                movieResponseDTO.setTheaterIds(grouped.get(movie));
                movieResponseDTOS.add(movieResponseDTO);
            }

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✔ Movies fetched Successfully!",
                            movieResponseDTOS,
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Movies are not fetched: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<MovieDTO>> getMovie(long id) {
        try{
            Movie movie = movieRepository.findBymovieId(id)
                    .orElseThrow(() ->  new Exception("Movie not found!"));

            List<MovieCastDTO> movieCastDTO = new ArrayList<>();
            List<MovieCast> movieCast = movie.getCast();


            for(MovieCast cast : movieCast){
                MovieCastDTO actor = new MovieCastDTO();
                PersonDTO person = new PersonDTO();
                person.setPersonId(cast.getPerson().getPersonId());
                actor.setPerson(person);
                movieCastDTO.add(actor);
            }

            List<GenreDTO> genreDTOS = new ArrayList<>();

            for(Genre genre : movie.getGenres()){
                GenreDTO genreDTO = new GenreDTO();
                genreDTO.setName(genre.getName());
                genreDTOS.add(genreDTO);
            }

            MovieDTO responseDTO = new MovieDTO();
            responseDTO.setRuntime(movie.getRuntime());
            responseDTO.setTitle(movie.getTitle());
            responseDTO.setLanguage(movie.getLanguage());
            responseDTO.setCast(movieCastDTO);
            responseDTO.setGenres(genreDTOS);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✔ Movie Details below!",
                            responseDTO,
                            LocalDateTime.now()
                    ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Movie Not found: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<MovieDTO>> createMovie(MovieDTO movieDTO) {
        try {
            log.info("Creat Movie Details: " + movieDTO.getTitle(), movieDTO.getGenres(), movieDTO.getLanguage());
            Movie movie = new Movie();
            movie.setTitle(movieDTO.getTitle());
            movie.setRuntime(movieDTO.getRuntime());
            movie.setLanguage(movieDTO.getLanguage());
            movie.setCBFC(movieDTO.getCBFC());

            movie = movieRepository.save(movie);

            List<Genre> finalGenres = new ArrayList<>();
            for (GenreDTO genre : movieDTO.getGenres()) {
                Genre existing = genreRepository.findByName(genre.getName());
                if(existing == null) {
                    Genre saveGenre = new Genre();
                    saveGenre.setName(genre.getName());
                    existing = genreRepository.save(saveGenre);
                }
                finalGenres.add(existing);
            }

            movie.setGenres(finalGenres);

            List<MovieCast>  finalCast = new ArrayList<>();
            for (MovieCastDTO movieCast : movieDTO.getCast()) {

                Person person =
                        personRepository.findByPersonId(movieCast.getPerson().getPersonId())
                                .orElseThrow(() -> new RuntimeException("Person not found in DB, Please create and save!"));

                MovieCast saveMovieCast = new MovieCast();
                saveMovieCast.setMovie(movie);
                saveMovieCast.setPerson(person);

                finalCast.add(saveMovieCast);
            }

            movie.setCast(finalCast);

            movieRepository.save(movie);
            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✔ Movie created succesfully, MovieID: " + movie.getMovieId(),
                            modelMapper.map(movie, MovieDTO.class),
                            LocalDateTime.now()
                    ));
        } catch (Exception e){
            log.error("Movie can't be created: " + e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Movie not created" + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<MovieUpdateResponseDTO>> updateMovie(long movieId, MovieUpdateResponseDTO movieDTO) {
        try {
            Movie movie = movieRepository.findBymovieId(movieId)
                    .orElseThrow(() -> new Exception("Movie not found! for the id: " + movieId));

            if(movieDTO.getTitle() != null) movie.setTitle(movieDTO.getTitle());
            if(movieDTO.getRuntime() != 0) movie.setRuntime(movieDTO.getRuntime());
            if(movieDTO.getLanguage() != null) movie.setLanguage(movieDTO.getLanguage());
            if(movieDTO.getCBFC() != null) movie.setCBFC(movieDTO.getCBFC());
            if(movieDTO.getRating() != 0) movie.setRating(movieDTO.getRating());
            if(movieDTO.getGenres() != null) {
                List<Genre> newGenres = genreService.updateGenres(movieDTO.getGenres());
                List<Genre> allGenres = new ArrayList<>(movie.getGenres());
                allGenres.addAll(newGenres);
                movie.setGenres(allGenres);
            }

            movie = movieRepository.save(movie);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Movie updated succesfully, MovieID: " + movie.getMovieId(),
                            modelMapper.map(movie, MovieUpdateResponseDTO.class),
                            LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Movie not updated: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteMovie(long movieId){
        try{
            Movie movie = movieRepository.findBymovieId(movieId)
                    .orElseThrow(() -> new Exception("Movie not found! for the id: " + movieId));

            List<Long> casts = movie.getCast().stream()
                    .map(MovieCast::getMovieCastId).toList();

            movieCastRepository.deleteMovieCastByMovieCastId(casts);
            movieRepository.delete(movie);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Movie deleted Succesfully",
                            "Deleted " + movie.getTitle(),
                            LocalDateTime.now()
                    ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(
                            400,
                            "❌ Movie not deleted: " + e.getMessage(),
                            null,
                            LocalDateTime.now()
                    ));
        }
    }
}
