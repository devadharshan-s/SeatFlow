package com.example.bookmyshow.movie.model;

import com.example.bookmyshow.constants.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
public class Review extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Review_id")
    private long reviewID;

    private String review;

    private int rating;

    @ManyToOne(fetch = FetchType.EAGER)
    private Movie movie;

    @JoinColumn(name = "Ticket_id")
    private Long ticketId;
}




