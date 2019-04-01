package com.leafyjava.tutorials.tccdemo.models;

import lombok.Data;

@Data
public class FlightReservationRequest {
    private String flightId;
    private Integer seats;
}
