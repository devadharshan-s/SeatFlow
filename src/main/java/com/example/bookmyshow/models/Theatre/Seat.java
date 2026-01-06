package com.example.bookmyshow.models.Theatre;

import com.example.bookmyshow.constants.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class  Seat extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Seat_id")
    private long seatId;

    private int seatNumber;

    @Column(name = "row_no")
    private char rowNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Screen_id", nullable = false)
    private Screen screen;

    private String category;

    private int price;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeat> showSeats = new ArrayList<>();

}
