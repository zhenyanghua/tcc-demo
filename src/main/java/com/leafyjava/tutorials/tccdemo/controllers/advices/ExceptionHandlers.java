package com.leafyjava.tutorials.tccdemo.controllers.advices;

import com.leafyjava.tutorials.tccdemo.exceptions.PartialConfirmationException;
import com.leafyjava.tutorials.tccdemo.exceptions.ReservationExpiredException;
import com.leafyjava.tutorials.tccdemo.exceptions.ResourceNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
        ReservationExpiredException.class,
        ResourceNotAvailableException.class,
        IllegalStateException.class
    })
    public void resourceNotFound(Exception e, HttpServletRequest request) {
        LOGGER.info("Resource not found: {}", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PartialConfirmationException.class)
    public void resourceConflict(PartialConfirmationException e, HttpServletRequest request) throws IOException {
        LOGGER.warn("Conflict when confirming all resources from request {}", e.getMessage());
        LOGGER.warn(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
    }
}
