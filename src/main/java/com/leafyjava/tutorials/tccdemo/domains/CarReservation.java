package com.leafyjava.tutorials.tccdemo.domains;

import com.leafyjava.tutorials.tccdemo.utils.enums.TccStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Entity
@Data
public class CarReservation {
    @Id
    private String id;
    private Integer stock;
    private String carCategory;
    private OffsetDateTime expireTime;
    private TccStatus status;
}
