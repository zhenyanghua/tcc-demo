package com.leafyjava.tutorials.tccdemo.controllers;

import com.leafyjava.tutorials.tccdemo.models.TccRequest;
import com.leafyjava.tutorials.tccdemo.services.CoordinatorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = CoordinatorController.BASE_PATH)
public class CoordinatorController {
    static final String BASE_PATH = "/coordinator/reservation";

    private CoordinatorService coordinatorService;

    public CoordinatorController(final CoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/confirm")
    public void confirm(@RequestBody TccRequest request) {
        coordinatorService.confirm(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cancel")
    public void cancel(@RequestBody TccRequest request) {
        coordinatorService.cancel(request);
    }
}
