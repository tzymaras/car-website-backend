package com.udacity.vehicles.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = CarNotFoundException.CAR_NOT_FOUND_MESSAGE)
public class CarNotFoundException extends RuntimeException {

    public static final String CAR_NOT_FOUND_MESSAGE = "car not found";

    public CarNotFoundException() {
    }

    public CarNotFoundException(String message) {
        super(message);
    }
}
