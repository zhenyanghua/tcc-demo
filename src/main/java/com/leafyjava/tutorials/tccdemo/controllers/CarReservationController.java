package com.leafyjava.tutorials.tccdemo.controllers;

import com.leafyjava.tutorials.tccdemo.domains.CarInventory;
import com.leafyjava.tutorials.tccdemo.domains.CarReservation;
import com.leafyjava.tutorials.tccdemo.models.CarReservationRequest;
import com.leafyjava.tutorials.tccdemo.models.Participant;
import com.leafyjava.tutorials.tccdemo.models.ReservationResponse;
import com.leafyjava.tutorials.tccdemo.services.CarReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.leafyjava.tutorials.tccdemo.utils.Constants.SERVER_URL;

@RestController
@RequestMapping(value = CarReservationController.BASE_PATH)
public class CarReservationController {
    static final String BASE_PATH = "/car/reservation";

    private CarReservationService reservationService;

    public CarReservationController(final CarReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ReservationResponse reserve(@RequestBody CarReservationRequest reservationRequest) {
        CarInventory carInventory = new CarInventory();
        carInventory.setCategory(reservationRequest.getCategory());
        carInventory.setStock(reservationRequest.getCount());

        CarReservation reservation = reservationService.trying(carInventory);
        String uri = String.format("%s%s/%s", SERVER_URL, BASE_PATH, reservation.getId());
        Participant participant = new Participant(uri, reservation.getExpireTime());

        return new ReservationResponse(participant);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reservationId}")
    public void cancel(@PathVariable String reservationId) {
        reservationService.cancel(reservationId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{reservationId}")
    public void confirm(@PathVariable String reservationId) {
        reservationService.confirm(reservationId);
    }
}
