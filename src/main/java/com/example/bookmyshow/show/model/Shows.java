package com.example.bookmyshow.show.model;

import com.example.bookmyshow.constants.AuditEntity;
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
    @Column(name = "show_id")
    private long showId;

    // 🔑 Cross-module references → IDs only
    @Column(name = "movie_id", nullable = false)
    private long movieId;

    @Column(name = "theatre_id", nullable = false)
    private long theatreId;

    @Column(name = "screen_id", nullable = false)
    private long screenId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // ✅ Same-module relationship is OK
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeat> showSeats = new ArrayList<>();
}




