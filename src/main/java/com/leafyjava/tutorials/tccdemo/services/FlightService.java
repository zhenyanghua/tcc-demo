package com.leafyjava.tutorials.tccdemo.services;

import com.google.common.base.Preconditions;
import com.leafyjava.tutorials.tccdemo.domains.FlightReservation;
import com.leafyjava.tutorials.tccdemo.domains.FlightSeat;
import com.leafyjava.tutorials.tccdemo.exceptions.ReservationExpiredException;
import com.leafyjava.tutorials.tccdemo.exceptions.ResourceNotAvailableException;
import com.leafyjava.tutorials.tccdemo.repositories.FlightReservationRepository;
import com.leafyjava.tutorials.tccdemo.utils.enums.TccStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightService implements TccCompliantService<FlightSeat, FlightReservation> {
    private FlightReservationRepository reservationRepository;

    public FlightService(final FlightReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public FlightReservation trying(final FlightSeat flightSeat) {
        Preconditions.checkNotNull(flightSeat);
        boolean onHold = reservationRepository.existsBySeatId(flightSeat.getId());
        if (onHold) {
            throw new ResourceNotAvailableException("This seat is currently not available");
        }
        FlightReservation reservation = new FlightReservation();
        reservation.setId(RandomStringUtils.randomAlphanumeric(6));
        reservation.setSeatId(flightSeat.getId());
        reservation.setStatus(TccStatus.TRY);
        reservation.setExpireTime(OffsetDateTime.now().plusSeconds(15));
        return reservationRepository.save(reservation);
    }

    @Scheduled(fixedRate = 1000)
    public void autoCancel() {
        List<FlightReservation> expiredReservation = reservationRepository.findByExpireTimeAfter(OffsetDateTime.now());
        expiredReservation.forEach(reservation -> cancel(reservation.getId()));
    }

    @Override
    @Transactional
    public void cancel(final String id) {
        Preconditions.checkNotNull(id);
        boolean exists = reservationRepository.existsById(id);
        if (!exists) {
            throw new ReservationExpiredException("Reservation " + id + " doesn't exist or has been cancelled.");
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public void confirm(final String id) {
        Preconditions.checkNotNull(id);
        FlightReservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationExpiredException("Reservation " + id + " doesn't exist or has been cancelled."));
        if (reservation.getStatus() == TccStatus.TRY) {
            reservation.setStatus(TccStatus.CONFIRM);
        }
    }
}
