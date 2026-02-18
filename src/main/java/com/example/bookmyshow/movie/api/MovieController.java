package com.example.bookmyshow.movie.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.movie.api.dto.MovieDTO;
import com.example.bookmyshow.movie.api.dto.MovieResponseDTO;
import com.example.bookmyshow.movie.api.dto.MovieUpdateDTO;
import com.example.bookmyshow.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/getAllMovies")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getAllMovies(  @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size){

        List<MovieDTO> response = movieService.getAllMovies(page, size);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Movies retrieved successfully!",
                        response,
                        java.time.LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getMovie/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> getMovie(@PathVariable long movieId){

        MovieResponseDTO response = movieService.getMovie(movieId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Movie retrieved successfully!",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/createMovie")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> createMovie(@RequestBody MovieDTO movie){

        MovieResponseDTO response = movieService.createMovie(movie);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Movie created successfully!",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping("/updateMovie/{movieId}")
    public ResponseEntity<ApiResponse<MovieUpdateDTO>> updateMovie(@RequestBody MovieUpdateDTO movie, @PathVariable long movieId){
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Movie updated successfully!",
                        movieService.updateMovie(movieId,movie),
                        LocalDateTime.now()
                ));
    }

    @DeleteMapping("/deleteMovie/{movieId}")
    public ResponseEntity<ApiResponse<?>> deleteMovies(@PathVariable long movieId){

        movieService.deleteMovie(movieId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✔ Movie deleted successfully!",
                        "MovieId: " + movieId,
                        LocalDateTime.now()
                )
        );
    }
}




