package com.leafyjava.tutorials.tccdemo.repositories;

import com.leafyjava.tutorials.tccdemo.domains.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, String> {
}
