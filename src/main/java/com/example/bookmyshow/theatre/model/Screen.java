package com.example.bookmyshow.theatre.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Screen_id")
    private long screenId;

    private String screenName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Theatre_id")
    private Theatre theatre;

    private int capacity;

    @Column(name = "Number_Of_Rows")
    private int numberOfRows;

    @ElementCollection
    private List<Long> showsId = new ArrayList<>();
}




