package org.example.bookmyshowshowservice.show.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "show_seat")
@Getter
@Setter
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_seat_id")
    private Long showSeatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Shows show;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "is_booked")
    private Boolean isBooked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Version
    private Integer version;
}
