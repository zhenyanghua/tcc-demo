package com.leafyjava.tutorials.tccdemo.models;

import lombok.Data;

@Data
public class CarReservationRequest {
    private String category;
    private Integer count;
}
