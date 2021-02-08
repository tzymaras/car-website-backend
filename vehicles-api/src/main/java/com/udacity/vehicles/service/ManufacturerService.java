package com.udacity.vehicles.service;

import com.udacity.vehicles.domain.manufacturer.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManufacturerService {
    private final ManufacturerRepository repository;

    public ManufacturerService(ManufacturerRepository repository) {
        this.repository = repository;
    }

    public Optional<Manufacturer> findByCodeAndName(Integer code, String name) {
        return this.repository.findManufacturerByCodeAndName(code, name);
    }
}
