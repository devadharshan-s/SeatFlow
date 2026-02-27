package org.example.bookmyshowmovieservice.movie.model;

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
@Table(name = "review")
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewID;

    @Column(name = "review")
    private String review;

    @Column(name = "rating")
    private int rating;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "ticket_id")
    private Long ticketId;
}
