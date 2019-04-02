package com.leafyjava.tutorials.tccdemo.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.leafyjava.tutorials.tccdemo.exceptions.PartialConfirmationException;
import com.leafyjava.tutorials.tccdemo.exceptions.ReservationExpiredException;
import com.leafyjava.tutorials.tccdemo.models.PartialConfirmationResponse;
import com.leafyjava.tutorials.tccdemo.models.Participant;
import com.leafyjava.tutorials.tccdemo.models.TccRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoordinatorService {
    private static HttpEntity<?> REQUEST;

    private RestTemplate restTemplate;

    static {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON_UTF8));
        REQUEST = new HttpEntity<>(headers);
    }

    public CoordinatorService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public void confirm(TccRequest request) {
        Preconditions.checkNotNull(request);
        List<Participant> participantLinks = request.getParticipantLinks();
        Preconditions.checkNotNull(participantLinks);

        boolean hasExpired = participantLinks.stream()
            .anyMatch(p -> p.getExpireTime().isBefore(OffsetDateTime.now()));
        if (hasExpired) {
            throw new ReservationExpiredException("Resource has expired or cancelled");
        }

        List<Participant> confirmedParticipants = new ArrayList<>();
        int failed = 0;

        for (Participant participant: participantLinks) {
            ResponseEntity<Object> responseEntity =
                restTemplate.exchange(participant.getUri(), HttpMethod.PUT, REQUEST, Object.class);

            Preconditions.checkNotNull(responseEntity);

            // resource confirmed
            if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
                confirmedParticipants.add(participant);
                continue;
            }
            // resource expired
            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                failed++;
                break;
            }
        }

        if (confirmedParticipants.size() == participantLinks.size()) {
            return;
        }

        // Attempt to cancel confirmed ones if partial confirmed, leave non-confirmed ones to expire
        if (failed > 0 && confirmedParticipants.size() > 0) {
            List<Participant> failedToBeRecovered = new ArrayList<>();

            for (Participant participant: confirmedParticipants) {
                ResponseEntity<String> responseEntity = restTemplate.exchange(participant.getUri(), HttpMethod.DELETE, null, String.class);
                if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                    failedToBeRecovered.add(participant);
                }
            }
            // Throw partial confirmation exception if failed to cancel those confirmed resources
            if (failedToBeRecovered.size() > 0) {
                throw new PartialConfirmationException(new PartialConfirmationResponse(failedToBeRecovered));
            }
        }

        throw new ReservationExpiredException("Resource has expired or cancelled");
    }

    // No need to do anything because all resources will eventually expire and thus auto-cancelled.
    public void cancel(TccRequest request) {
        Preconditions.checkNotNull(request);
        request.getParticipantLinks()
            .forEach(participant -> restTemplate.delete(participant.getUri()));
    }
}
