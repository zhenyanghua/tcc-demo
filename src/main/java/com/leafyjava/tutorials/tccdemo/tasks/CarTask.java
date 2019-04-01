package com.leafyjava.tutorials.tccdemo.tasks;

import com.leafyjava.tutorials.tccdemo.domains.CarReservation;
import com.leafyjava.tutorials.tccdemo.repositories.CarReservationRepository;
import com.leafyjava.tutorials.tccdemo.services.CarService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CarTask implements TccCompliantTask {
    private CarReservationRepository reservationRepository;
    private CarService carService;

    public CarTask(final CarReservationRepository reservationRepository,
                   final CarService carService) {
        this.reservationRepository = reservationRepository;
        this.carService = carService;
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void autoCancel() {
        List<CarReservation> expiredReservations = reservationRepository.findByExpireTimeAfter(OffsetDateTime.now());
        expiredReservations.forEach(reservation -> carService.cancel(reservation));
    }
}
