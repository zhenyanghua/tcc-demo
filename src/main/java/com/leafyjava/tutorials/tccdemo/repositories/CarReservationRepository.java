package com.leafyjava.tutorials.tccdemo.repositories;

import com.leafyjava.tutorials.tccdemo.domains.CarReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarReservationRepository extends JpaRepository<CarReservation, String> {
}
