package com.leafyjava.tutorials.tccdemo.domains;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Flight {
    @Id
    private String id;
    private Integer availableSeats;
}
