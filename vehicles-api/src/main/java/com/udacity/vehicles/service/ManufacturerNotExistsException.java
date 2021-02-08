package com.udacity.vehicles.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = ManufacturerNotExistsException.NOT_EXISTS_MESSAGE)
public class ManufacturerNotExistsException extends RuntimeException {
    public static final String NOT_EXISTS_MESSAGE = "Manufacturer does not exist";

    public ManufacturerNotExistsException() {
    }

    public ManufacturerNotExistsException(String message) {
        super(message);
    }
}
