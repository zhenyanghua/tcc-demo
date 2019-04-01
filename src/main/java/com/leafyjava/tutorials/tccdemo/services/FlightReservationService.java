package com.leafyjava.tutorials.tccdemo.services;

import com.google.common.base.Preconditions;
import com.leafyjava.tutorials.tccdemo.domains.Flight;
import com.leafyjava.tutorials.tccdemo.domains.FlightReservation;
import com.leafyjava.tutorials.tccdemo.exceptions.ReservationExpiredException;
import com.leafyjava.tutorials.tccdemo.exceptions.ResourceNotAvailableException;
import com.leafyjava.tutorials.tccdemo.repositories.FlightRepository;
import com.leafyjava.tutorials.tccdemo.repositories.FlightReservationRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static com.leafyjava.tutorials.tccdemo.utils.enums.TccStatus.CONFIRM;
import static com.leafyjava.tutorials.tccdemo.utils.enums.TccStatus.TRY;

@Service
public class FlightReservationService implements TccCompliantService<Flight, FlightReservation> {
    private FlightReservationRepository reservationRepository;
    private FlightRepository flightRepository;

    public FlightReservationService(final FlightReservationRepository reservationRepository,
                                    final FlightRepository flightRepository) {
        this.reservationRepository = reservationRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    @Transactional
    public FlightReservation trying(final Flight flight) {
        Preconditions.checkNotNull(flight);
        Preconditions.checkNotNull(flight.getId());

        Flight flightAvailable = flightRepository.findById(flight.getId())
            .orElseThrow(() -> new IllegalStateException("Can't find flight id " + flight.getId() + " from the scheduled flights"));

        if (flight.getAvailableSeats() > flightAvailable.getAvailableSeats()) {
            throw new ResourceNotAvailableException("There is not enough seats available in flight " + flight.getId());
        }

        flightAvailable.setAvailableSeats(flightAvailable.getAvailableSeats() - flight.getAvailableSeats());
        flightRepository.save(flightAvailable);

        FlightReservation reservation = new FlightReservation();
        reservation.setSeats(flight.getAvailableSeats());
        reservation.setFlightId(flight.getId());
        reservation.setId(RandomStringUtils.randomAlphanumeric(6));
        reservation.setExpireTime(OffsetDateTime.now().plusSeconds(15));
        reservation.setStatus(TRY);

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void cancel(final String id) {
        Preconditions.checkNotNull(id);

        FlightReservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationExpiredException("Reservation " + id + " has been cancelled or doesn't exist"));
        cancel(reservation);
    }

    @Transactional
    public void cancel(final FlightReservation reservation) {
        Preconditions.checkNotNull(reservation);
        Preconditions.checkNotNull(reservation.getId());
        Preconditions.checkNotNull(reservation.getFlightId());
        Preconditions.checkNotNull(reservation.getStatus());

        if (reservation.getStatus() == TRY) {
            reservationRepository.deleteById(reservation.getId());
            Flight flight = flightRepository.findById(reservation.getFlightId())
                .orElseThrow(() -> new IllegalStateException("Can't find flight id " + reservation.getFlightId() + " from the scheduled flights"));
            flight.setAvailableSeats(flight.getAvailableSeats() + reservation.getSeats());
            flightRepository.save(flight);
        }
    }

    @Override
    @Transactional
    public void confirm(final String id) {
        Preconditions.checkNotNull(id);
        FlightReservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationExpiredException("Reservation " + id + " has been cancelled or doesn't exist"));

        if (reservation.getStatus() == TRY) {
            reservation.setStatus(CONFIRM);
            reservationRepository.save(reservation);
        }
    }
}
