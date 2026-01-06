package com.example.bookmyshow.DTO.TheatreDTO;

import lombok.Data;

@Data
public class ScreenDTO {

    private String screenName;
    private int theatreId;
    private int capacity;
    private int numberOfRows;
}
