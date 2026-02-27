package org.example.bookmyshowshowservice.show.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shows")
@Getter
@Setter
public class Shows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_id")
    private Long showId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "theatre_id", nullable = false)
    private Long theatreId;

    @Column(name = "screen_id", nullable = false)
    private Long screenId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeat> showSeats = new ArrayList<>();
}
