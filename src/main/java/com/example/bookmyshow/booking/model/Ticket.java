package com.example.bookmyshow.booking.model;

import com.example.bookmyshow.constants.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Ticket extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ticket_id")
    private long ticketId;

    @ElementCollection
    private List<Long> showSeatIds;

    @JoinColumn(name = "Show_id")
    private Long showId;

    @JoinColumn(name = "User_id")
    private Long userId;

    private double amountPaid;
}




