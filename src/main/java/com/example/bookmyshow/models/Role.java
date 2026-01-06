package com.example.bookmyshow.models;

import com.example.bookmyshow.constants.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Role extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Role_id")
    private int roleId;

    private String roleName;
}
