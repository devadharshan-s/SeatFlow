package com.example.bookmyshow.DTO.TheatreDTO.ShowDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowResponseDTO {
    private long showId;
    private long theatreId;
    private long screenId;
    private long movieId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endTime;
}
