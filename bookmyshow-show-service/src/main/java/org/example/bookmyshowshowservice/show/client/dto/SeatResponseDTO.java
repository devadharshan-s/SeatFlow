package org.example.bookmyshowshowservice.show.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatResponseDTO {
    private Long seatId;
    private Integer seatNumber;
    private String rowNumber;
    private Integer price;
    private String category;
}
