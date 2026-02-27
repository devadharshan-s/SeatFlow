package com.example.bookmyshow.show.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.show.api.dto.ShowDTO;
import com.example.bookmyshow.show.api.dto.ShowResponseDTO;
import com.example.bookmyshow.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShowsController {

    private final ShowService showService;

    @GetMapping("/getAllShows")
    public ResponseEntity<ApiResponse<List<ShowResponseDTO>>> getAllShows(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        List<ShowResponseDTO> shows = showService.getAllShows(page, size);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Shows fetched successfully",
                        shows,
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/getShowById")
    public ResponseEntity<ApiResponse<ShowDTO>> getShowById(@RequestParam long showId){
        ShowDTO response =  showService.getShowById(showId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Show fetched successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/createShow")
    public ResponseEntity<ApiResponse<ShowDTO>> addShow(@RequestBody ShowDTO show){
        ShowDTO response = showService.createShows(show);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Show created successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping("/updateShow/{showId}")
    public ResponseEntity<ApiResponse<ShowResponseDTO>> updateShow(@PathVariable long showId, @RequestBody ShowDTO show){
        ShowResponseDTO response = showService.updateShow(showId, show);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Show updated successfully",
                        response,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("/deleteShow")
    public void deleteShow(@RequestParam long showId){
        showService.removeShows(showId);
    }
}








