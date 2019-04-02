package com.leafyjava.tutorials.tccdemo.tasks;

import com.leafyjava.tutorials.tccdemo.domains.CarReservation;
import com.leafyjava.tutorials.tccdemo.repositories.CarReservationRepository;
import com.leafyjava.tutorials.tccdemo.services.CarReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CarTask implements TccCompliantTask {
    private CarReservationRepository reservationRepository;
    private CarReservationService carReservationService;

    public CarTask(final CarReservationRepository reservationRepository,
                   final CarReservationService carReservationService) {
        this.reservationRepository = reservationRepository;
        this.carReservationService = carReservationService;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void autoCancel() {
        List<CarReservation> expiredReservations = reservationRepository.findByExpireTimeBefore(OffsetDateTime.now());
        expiredReservations.forEach(reservation -> carReservationService.cancel(reservation));
    }
}
