package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.*;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import org.modelmapper.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {
    private final CarRepository carRepository;
    private final ManufacturerService manufacturerService;
    private final ModelMapper modelMapper;
    private final PriceClient pricesWebClient;
    private final MapsClient mapsWebClient;

    public CarService(
            CarRepository carRepository,
            ManufacturerService manufacturerService,
            ModelMapper modelMapper,
            PriceClient pricesWebClient,
            MapsClient mapsWebClient
    ) {
        this.carRepository = carRepository;
        this.manufacturerService = manufacturerService;
        this.modelMapper = modelMapper;
        this.pricesWebClient = pricesWebClient;
        this.mapsWebClient = mapsWebClient;
    }

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return this.carRepository.findAll()
                .stream()
                .peek(this::addPrice)
                .peek(this::addLocation)
                .collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = this.carRepository.findById(id).orElseThrow(CarNotFoundException::new);

        this.addPrice(car);
        this.addLocation(car);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        Manufacturer manufacturer = car.getDetails().getManufacturer();

        this.manufacturerService
                .findByCodeAndName(manufacturer.getCode(), manufacturer.getName())
                .orElseThrow(ManufacturerNotExistsException::new);

        this.addLocation(car);
        this.addPrice(car);

        if (car.getId() != null) {
            return carRepository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        this.modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
                        this.modelMapper.map(car.getDetails(), carToBeUpdated.getDetails());
                        this.modelMapper.map(car.getLocation(), carToBeUpdated.getLocation());
                        this.modelMapper.map(car, carToBeUpdated);

                        return carRepository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return carRepository.save(car);
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        this.carRepository
                .findById(id)
                .ifPresentOrElse(
                        car -> this.carRepository.deleteById(id),
                        CarNotFoundException::new
                );
    }

    private void addLocation(Car car) {
        car.setLocation(this.mapsWebClient.getAddress(car.getLocation()));
    }

    private void addPrice(Car car) {
        car.setPrice(this.pricesWebClient.getPrice(car.getId()));
    }
}
