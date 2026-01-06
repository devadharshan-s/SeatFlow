package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.MovieDTO.MovieDTO;
import com.example.bookmyshow.DTO.MovieDTO.MovieResponseDTO;
import com.example.bookmyshow.DTO.MovieDTO.MovieUpdateResponseDTO;
import com.example.bookmyshow.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/getAllMovies")
    public ResponseEntity<ApiResponse<List<MovieResponseDTO>>> getAllMovies(){
        return movieService.getAllMovies();
    }

    @GetMapping("/getMovie/{movieId}")
    public ResponseEntity<ApiResponse<MovieDTO>> getMovie(@PathVariable long movieId){
        return movieService.getMovie(movieId);
    }

    @PostMapping("/createMovie")
    public ResponseEntity<ApiResponse<MovieDTO>> createMovie(@RequestBody MovieDTO movie){
        return movieService.createMovie(movie);
//        if(result == null){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(
//                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                            "❌ Movie can't be created!",
//                            result,
//                            LocalDateTime.now()
//                    ));
//        } else {
//            return ResponseEntity.ok(new ApiResponse<>(
//                    HttpStatus.CREATED.value(),
//                    "✅ Movie Created Successfully!",
//                    result,
//                    LocalDateTime.now()
//            ));
//        }
    }

    @PatchMapping("/updateMovie/{movieId}")
    public ResponseEntity<ApiResponse<MovieUpdateResponseDTO>> updateMovie(@RequestBody MovieUpdateResponseDTO movie, @PathVariable long movieId){
        return movieService.updateMovie(movieId, movie);
    }

    @DeleteMapping("/deleteMovie/{movieId}")
    public ResponseEntity<ApiResponse<?>> deleteMovies(@PathVariable long movieId){
        return movieService.deleteMovie(movieId);
    }
}
