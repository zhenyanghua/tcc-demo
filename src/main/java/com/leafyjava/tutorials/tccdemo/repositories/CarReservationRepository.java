package com.leafyjava.tutorials.tccdemo.repositories;

import com.leafyjava.tutorials.tccdemo.domains.CarReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CarReservationRepository extends JpaRepository<CarReservation, String> {
    List<CarReservation> findByExpireTimeAfter(OffsetDateTime time);
}
