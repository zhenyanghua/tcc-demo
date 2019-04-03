package com.leafyjava.tutorials.tccdemo;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.leafyjava.tutorials.tccdemo.models.CarReservationRequest;
import com.leafyjava.tutorials.tccdemo.models.FlightReservationRequest;
import com.leafyjava.tutorials.tccdemo.models.PartialConfirmationResponse;
import com.leafyjava.tutorials.tccdemo.models.Participant;
import com.leafyjava.tutorials.tccdemo.models.ReservationResponse;
import com.leafyjava.tutorials.tccdemo.models.TccRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.leafyjava.tutorials.tccdemo.utils.Constants.SERVER_URL;

@Component
public class ExampleWorkFlow {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleWorkFlow.class);

    private RestTemplate restTemplate;

    public ExampleWorkFlow(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doWork() {
        LOGGER.info("=============== 1st Book Attempt =============");
        LOGGER.info("Reserve 2 flight seats");
        Participant flightLink = bookFlight("UA375", 2);
        LOGGER.info("Reserve 1 car");
        Participant carLink = bookCar("economy", 1);
        LOGGER.info("Cancel flight");
        cancel(flightLink);

        TccRequest tccRequest = new TccRequest();
        tccRequest.setParticipantLinks(ImmutableList.of(flightLink, carLink));
        confirm(tccRequest);

        LOGGER.info("=============== 2nd Book Attempt =============");
        LOGGER.info("Reserve 2 flight seats");
        flightLink = bookFlight("UA375", 2);
        LOGGER.info("Reserve 1 car");
        carLink = bookCar("economy", 1);
//        LOGGER.info("Cancel flight");
//        cancel(flightLink);

        tccRequest = new TccRequest();
        tccRequest.setParticipantLinks(ImmutableList.of(flightLink, carLink));
        confirm(tccRequest);

    }

    private Participant bookFlight(String id, int seats) {
        FlightReservationRequest flightReservationRequest = new FlightReservationRequest();
        flightReservationRequest.setFlightId(id);
        flightReservationRequest.setSeats(seats);

        ReservationResponse reservationResponse = restTemplate.postForObject(
            String.format("%s/flight/reservation", SERVER_URL),
            flightReservationRequest,
            ReservationResponse.class);
        Preconditions.checkNotNull(reservationResponse);

        return reservationResponse.getParticipantLink();
    }

    private Participant bookCar(String category, int count) {
        CarReservationRequest carReservationRequest = new CarReservationRequest();
        carReservationRequest.setCategory(category);
        carReservationRequest.setCount(count);

        ReservationResponse reservationResponse = restTemplate.postForObject(
            String.format("%s/car/reservation", SERVER_URL),
            carReservationRequest,
            ReservationResponse.class);
        Preconditions.checkNotNull(reservationResponse);

        return reservationResponse.getParticipantLink();
    }

    private void confirm(TccRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON_UTF8));
        HttpEntity<TccRequest> tccRequestHttpEntity = new HttpEntity<>(request, headers);


        ResponseEntity<Object> responseEntity = restTemplate.exchange(
            String.format("%s/coordinator/reservation/confirm", SERVER_URL),
            HttpMethod.PUT,
            tccRequestHttpEntity,
            Object.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.NOT_FOUND) {
            LOGGER.info("Confirm failed - {}", statusCode);
        } else if (statusCode == HttpStatus.NO_CONTENT) {
            LOGGER.info("Confirm succeeded");
        } else if (statusCode == HttpStatus.CONFLICT) {
            Preconditions.checkNotNull(responseEntity);
            Preconditions.checkNotNull(responseEntity.getBody());
            PartialConfirmationResponse response = (PartialConfirmationResponse) responseEntity.getBody();
            List<Participant> participantLinks = response.getParticipantLinks();
            String resources = participantLinks.stream()
                .map(p -> String.format("url: %s, exp: %s", p.getUri(), p.getExpireTime()))
                .collect(Collectors.joining(System.lineSeparator()));
            LOGGER.info("Conflict when confirming the following resources", resources);
        }
    }

    private void cancel(Participant participant) {
        restTemplate.delete(participant.getUri());
    }
}
