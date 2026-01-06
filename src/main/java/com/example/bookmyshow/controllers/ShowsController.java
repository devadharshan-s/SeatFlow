package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.TheatreDTO.ShowDTO.ShowDTO;
import com.example.bookmyshow.DTO.TheatreDTO.ShowDTO.ShowResponseDTO;
import com.example.bookmyshow.services.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShowsController {

    private final ShowService showService;

    @GetMapping("/getAllShows")
    public ResponseEntity<ApiResponse<List<ShowResponseDTO>>> getAllShows(){
        return showService.getAllShows();
    }

    @GetMapping("/getShowById")
    public ResponseEntity<ApiResponse<ShowDTO>> getShowById(@RequestParam long showId){
        return showService.getShowById(showId);
    }

    @PostMapping("/createShow")
    public ResponseEntity<ApiResponse<ShowDTO>> addShow(@RequestBody ShowDTO show){
        return showService.createShows(show);
    }

    @PatchMapping("/updateShow/{showId}")
    public ResponseEntity<ApiResponse<ShowResponseDTO>> updateShow(@PathVariable long showId, @RequestBody ShowDTO show){
        return showService.updateShow(showId, show);
    }

    @DeleteMapping("/deleteShow")
    public ResponseEntity<ApiResponse<?>> deleteShow(@RequestParam long showId){
        return showService.removeShows(showId);
    }
}
