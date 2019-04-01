package com.leafyjava.tutorials.tccdemo.domains;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class CarInventory {
    @Id
    private String category;
    private Integer stock;
}
