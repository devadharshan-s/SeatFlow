package com.example.bookmyshow.models.Theatre;

import com.example.bookmyshow.constants.AuditEntity;
import com.example.bookmyshow.models.Movie.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Shows extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Show_id")
    private long showId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Screen_id", nullable = false)
    private Screen screen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Theatre_id", nullable = false)
    private Theatre theatre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Movie_id")
    private Movie movie;

    @Column(nullable = false)
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Column(nullable = false)
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeat> showSeats = new ArrayList<>();

}
