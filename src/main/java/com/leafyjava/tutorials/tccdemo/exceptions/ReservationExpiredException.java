package com.leafyjava.tutorials.tccdemo.exceptions;

public class ReservationExpiredException extends RuntimeException {
    public ReservationExpiredException() {
    }

    public ReservationExpiredException(final String message) {
        super(message);
    }
}
