package com.leafyjava.tutorials.tccdemo.exceptions;

import com.leafyjava.tutorials.tccdemo.models.PartialConfirmationResponse;
import lombok.Getter;

@Getter
public class PartialConfirmationException extends RuntimeException {
    private PartialConfirmationResponse response;

    public PartialConfirmationException() {
    }

    public PartialConfirmationException(final String message) {
        super(message);
    }

    public PartialConfirmationException(final Throwable cause) {
        super(cause);
    }

    public PartialConfirmationException(PartialConfirmationResponse response) {
        this.response = response;
    }

    public PartialConfirmationException(final String message, PartialConfirmationResponse response) {
        super(message);
        this.response = response;
    }
}
