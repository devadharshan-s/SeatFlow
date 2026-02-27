package com.example.bookmyshow.theatre.api.dto;

import lombok.Data;

@Data
public class ScreenResponseDTO {

    private long screenId;
    private String screenName;
    private int theatreId;
    private int capacity;
    private int numberOfRows;
}




