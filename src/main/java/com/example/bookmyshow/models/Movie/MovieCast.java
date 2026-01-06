package com.example.bookmyshow.models.Movie;

import com.example.bookmyshow.constants.BaseEntity;
import com.example.bookmyshow.models.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class MovieCast extends BaseEntity {

//    @EmbeddedId
//    private MovieCastId movieCastId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Movie_cast_id")
    private long movieCastId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Person_id")
    private Person person;

}
