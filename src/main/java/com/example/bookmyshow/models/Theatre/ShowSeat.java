package com.example.bookmyshow.models.Theatre;

import com.example.bookmyshow.models.Ticket;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Seat_id", nullable = false)
    private Seat seat;

    @Column(name = "isBooked")
    private Boolean isBooked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Ticket_id")
    private Ticket ticket;

    @Version
    private Integer version;
}
