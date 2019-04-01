package com.leafyjava.tutorials.tccdemo.domains;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class FlightSeat {
    private String flight;
    private String id;
}
