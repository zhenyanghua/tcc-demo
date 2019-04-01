package com.leafyjava.tutorials.tccdemo.repositories;

import com.leafyjava.tutorials.tccdemo.domains.CarInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<CarInventory, String> {
}
