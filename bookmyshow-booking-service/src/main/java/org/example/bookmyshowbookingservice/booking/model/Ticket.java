package org.example.bookmyshowbookingservice.booking.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ElementCollection
    @CollectionTable(name = "ticket_show_seats", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "show_seat_id")
    private List<Long> showSeatIds;

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount_paid")
    private double amountPaid;
}
