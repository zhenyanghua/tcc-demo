package com.leafyjava.tutorials.tccdemo.exceptions;

public class ResourceNotAvailableException extends RuntimeException {
    public ResourceNotAvailableException() {
    }

    public ResourceNotAvailableException(final String message) {
        super(message);
    }
}
