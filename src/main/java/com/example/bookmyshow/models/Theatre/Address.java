package com.example.bookmyshow.models.Theatre;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Address_id")
    private long addressId;

    @Column(name = "Building_Number")
    private int buildingNumber;

    private String Block;

    private String Street;

    private String City;

    private String District;

    private String State;

    private String Country;

    @Column(name = "pincode")
    private int pinCode;
}
