package com.example.bookmyshow.DTO.TheatreDTO;

import lombok.Data;

@Data
public class ScreenResponseDTO {

    private long screenId;
    private String screenName;
    private int theatreId;
    private int capacity;
    private int numberOfRows;
}
