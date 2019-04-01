package com.leafyjava.tutorials.tccdemo.repositories;

import com.leafyjava.tutorials.tccdemo.domains.FlightReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface FlightReservationRepository extends JpaRepository<FlightReservation, String> {
    List<FlightReservation> findByExpireTimeAfter(OffsetDateTime time);
}
