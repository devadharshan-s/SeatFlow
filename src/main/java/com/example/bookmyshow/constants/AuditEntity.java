package com.example.bookmyshow.constants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class AuditEntity extends BaseEntity {

    @LastModifiedDate
    @JsonIgnore
    @Column(name = "update_dt", insertable = false)
    private LocalDateTime updatedt;
}
