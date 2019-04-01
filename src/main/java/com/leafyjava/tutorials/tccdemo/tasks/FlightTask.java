package com.leafyjava.tutorials.tccdemo.tasks;

import com.leafyjava.tutorials.tccdemo.domains.FlightReservation;
import com.leafyjava.tutorials.tccdemo.repositories.FlightReservationRepository;
import com.leafyjava.tutorials.tccdemo.services.FlightService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightTask implements TccCompliantTask {
    private FlightReservationRepository reservationRepository;
    private FlightService flightService;

    public FlightTask(final FlightReservationRepository reservationRepository,
                      final FlightService flightService) {
        this.reservationRepository = reservationRepository;
        this.flightService = flightService;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void autoCancel() {
        List<FlightReservation> expiredReservations = reservationRepository.findByExpireTimeAfter(OffsetDateTime.now());
        expiredReservations.forEach(reservation -> flightService.cancel(reservation));
    }
}
