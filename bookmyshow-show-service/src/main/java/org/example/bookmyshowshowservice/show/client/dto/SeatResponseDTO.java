package org.example.bookmyshowshowservice.show.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SeatResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long seatId;
    private Integer seatNumber;
    private String rowNumber;
    private Integer price;
    private String category;
}
