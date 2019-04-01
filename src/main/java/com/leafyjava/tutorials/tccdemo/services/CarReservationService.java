package com.leafyjava.tutorials.tccdemo.services;

import com.google.common.base.Preconditions;
import com.leafyjava.tutorials.tccdemo.domains.CarInventory;
import com.leafyjava.tutorials.tccdemo.domains.CarReservation;
import com.leafyjava.tutorials.tccdemo.exceptions.ReservationExpiredException;
import com.leafyjava.tutorials.tccdemo.exceptions.ResourceNotAvailableException;
import com.leafyjava.tutorials.tccdemo.repositories.CarRepository;
import com.leafyjava.tutorials.tccdemo.repositories.CarReservationRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static com.leafyjava.tutorials.tccdemo.utils.enums.TccStatus.CONFIRM;
import static com.leafyjava.tutorials.tccdemo.utils.enums.TccStatus.TRY;

@Service
public class CarReservationService implements TccCompliantService<CarInventory, CarReservation> {
    private CarReservationRepository reservationRepository;
    private CarRepository carRepository;

    public CarReservationService(final CarReservationRepository reservationRepository,
                                 final CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional
    public CarReservation trying(final CarInventory carInventory) {
        Preconditions.checkNotNull(carInventory);
        Preconditions.checkNotNull(carInventory.getCategory());

        CarInventory carInventoryAvailable = carRepository.findById(carInventory.getCategory())
            .orElseThrow(() -> new IllegalStateException("Can't find car category " + carInventory.getCategory() + " in the inventory"));

        if (carInventory.getStock() > carInventoryAvailable.getStock()) {
            throw new ResourceNotAvailableException("Car category " + carInventory.getCategory() + " is out of stock");
        }

        carInventoryAvailable.setStock(carInventoryAvailable.getStock() - carInventory.getStock());
        carRepository.save(carInventoryAvailable);

        CarReservation reservation = new CarReservation();
        reservation.setStock(carInventory.getStock());
        reservation.setCarCategory(carInventory.getCategory());
        reservation.setId(RandomStringUtils.randomAlphanumeric(6));
        reservation.setExpireTime(OffsetDateTime.now().plusSeconds(15));
        reservation.setStatus(TRY);

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void cancel(final String id) {
        Preconditions.checkNotNull(id);

        CarReservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationExpiredException("Reservation " + id + " has been cancelled or doesn't exist"));
        cancel(reservation);
    }

    @Transactional
    public void cancel(final CarReservation reservation) {
        Preconditions.checkNotNull(reservation);
        Preconditions.checkNotNull(reservation.getId());
        Preconditions.checkNotNull(reservation.getCarCategory());
        Preconditions.checkNotNull(reservation.getStatus());

        if (reservation.getStatus() == TRY) {
            reservationRepository.deleteById(reservation.getId());
            CarInventory carInventory = carRepository.findById(reservation.getCarCategory())
                .orElseThrow(() -> new IllegalStateException("Can't find car category " + reservation.getCarCategory() + " in the inventory"));
            carInventory.setStock(carInventory.getStock() + reservation.getStock());
            carRepository.save(carInventory);
        }
    }

    @Override
    @Transactional
    public void confirm(final String id) {
        Preconditions.checkNotNull(id);
        CarReservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationExpiredException("Reservation " + id + " has been cancelled or doesn't exist"));

        if (reservation.getStatus() == TRY) {
            reservation.setStatus(CONFIRM);
            reservationRepository.save(reservation);
        }
    }
}
