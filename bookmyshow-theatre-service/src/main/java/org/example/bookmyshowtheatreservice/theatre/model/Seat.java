package org.example.bookmyshowtheatreservice.theatre.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "seat_number")
    private int seatNumber;

    @Column(name = "row_no")
    private char rowNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "category")
    private String category;

    @Column(name = "price")
    private int price;
}
