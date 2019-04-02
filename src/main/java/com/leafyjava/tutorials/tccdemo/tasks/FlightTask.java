package com.leafyjava.tutorials.tccdemo.tasks;

import com.leafyjava.tutorials.tccdemo.domains.FlightReservation;
import com.leafyjava.tutorials.tccdemo.repositories.FlightReservationRepository;
import com.leafyjava.tutorials.tccdemo.services.FlightReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightTask implements TccCompliantTask {
    private FlightReservationRepository reservationRepository;
    private FlightReservationService flightReservationService;

    public FlightTask(final FlightReservationRepository reservationRepository,
                      final FlightReservationService flightReservationService) {
        this.reservationRepository = reservationRepository;
        this.flightReservationService = flightReservationService;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void autoCancel() {
        List<FlightReservation> expiredReservations = reservationRepository.findByExpireTimeBefore(OffsetDateTime.now());
        expiredReservations.forEach(reservation -> flightReservationService.cancel(reservation));
    }
}
