package com.example.bookmyshow.show.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long showSeatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Show_id", nullable = false)
    private Shows show;

    @JoinColumn(name = "Seat_id", nullable = false)
    private Long seatId;

    @Column(name = "isBooked")
    private Boolean isBooked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @JoinColumn(name = "Ticket_id")
    private Long ticketId;

    @Version
    private Integer version;
}




