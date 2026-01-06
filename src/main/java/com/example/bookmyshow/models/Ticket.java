package com.example.bookmyshow.models;

import com.example.bookmyshow.constants.AuditEntity;
import com.example.bookmyshow.models.Theatre.ShowSeat;
import com.example.bookmyshow.models.Theatre.Shows;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Ticket extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ticket_id")
    private long ticketId;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER)
    private List<ShowSeat> seat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Show_id")
    private Shows show;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "User_id")
    private User user;

    private double amountPaid;
}
