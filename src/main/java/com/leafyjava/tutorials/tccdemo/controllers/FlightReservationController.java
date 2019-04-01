package com.leafyjava.tutorials.tccdemo.controllers;

import com.leafyjava.tutorials.tccdemo.domains.Flight;
import com.leafyjava.tutorials.tccdemo.domains.FlightReservation;
import com.leafyjava.tutorials.tccdemo.models.FlightReservationRequest;
import com.leafyjava.tutorials.tccdemo.models.Participant;
import com.leafyjava.tutorials.tccdemo.models.ReservationResponse;
import com.leafyjava.tutorials.tccdemo.services.FlightReservationService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.leafyjava.tutorials.tccdemo.utils.Constants.SERVER_URL;

@RestController
@RequestMapping(value = FlightReservationController.BASE_PATH)
public class FlightReservationController {
    static final String BASE_PATH = "/flight/reservation";

    private FlightReservationService flightReservationService;

    public FlightReservationController(final FlightReservationService flightReservationService) {
        this.flightReservationService = flightReservationService;
    }

    @PostMapping("/reserve")
    public ReservationResponse reserve(@RequestBody FlightReservationRequest reservationRequest) {
        Flight flight = new Flight();
        flight.setAvailableSeats(reservationRequest.getSeats());
        flight.setId(reservationRequest.getFlightId());

        FlightReservation reservation = flightReservationService.trying(flight);
        String url = String.format("%s/%s/%s", SERVER_URL, BASE_PATH, reservation.getId());
        Participant participant = new Participant(url, reservation.getExpireTime());

        return new ReservationResponse(participant);
    }

    @DeleteMapping("/cancel")
    public void cancel(@PathVariable String reservationId) {
        flightReservationService.cancel(reservationId);
    }

    @PutMapping("/confirm")
    public void confirm(@PathVariable String reservationId) {
        flightReservationService.confirm(reservationId);
    }
}
