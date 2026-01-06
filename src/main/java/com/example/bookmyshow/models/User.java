package com.example.bookmyshow.models;

import com.example.bookmyshow.constants.AuditEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id", nullable = false)
    private int userId;

    @Column(name = "Keycloak_Id")
    private String keyCloakId;

    @Column(name = "username")
    private String userName;

    private String email;

    private String phone;

    @JsonIgnore
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Role_id", referencedColumnName = "Role_id")
    private Role role;

}
