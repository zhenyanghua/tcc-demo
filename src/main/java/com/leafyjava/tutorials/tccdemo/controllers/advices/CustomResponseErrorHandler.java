package com.leafyjava.tutorials.tccdemo.controllers.advices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomResponseErrorHandler.class);

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return false;
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        LOGGER.info("Error response '{}'", response);
    }
}
