package org.example.bookmyshowtheatreservice.theatre.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screen")
@Getter
@Setter
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long screenId;

    @Column(name = "screen_name")
    private String screenName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "number_of_rows")
    private int numberOfRows;

    @ElementCollection
    private List<Long> showsId = new ArrayList<>();
}
