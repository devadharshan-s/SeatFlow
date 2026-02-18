package com.example.bookmyshow.theatre.model;

import com.example.bookmyshow.constants.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Theatre extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Theatre_id")
    private long theatreId;

    @Column(name = "Name")
    private String theatreName;

    private String contact;

    private String email;

    //pls add this in schema
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Address_id")
    private Address address;
}




