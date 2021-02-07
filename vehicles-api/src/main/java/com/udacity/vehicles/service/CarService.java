package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.*;
import org.modelmapper.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {
    private final CarRepository repository;
    private final ModelMapper modelMapper;
    private final PriceClient pricesWebClient;
    private final MapsClient mapsWebClient;

    public CarService(
            CarRepository repository,
            ModelMapper modelMapper,
            PriceClient pricesWebClient,
            MapsClient mapsWebClient
    ) {
        this.repository = repository;
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
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = this.repository.findById(id).orElseThrow(CarNotFoundException::new);
        car.setPrice(this.pricesWebClient.getPrice(id));

        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */


        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        this.modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
                        this.modelMapper.map(car.getDetails(), carToBeUpdated.getDetails());
                        this.modelMapper.map(car.getLocation(), carToBeUpdated.getLocation());
                        this.modelMapper.map(car, carToBeUpdated);
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        this.repository
                .findById(id)
                .ifPresentOrElse(
                        car -> this.repository.deleteById(id),
                        CarNotFoundException::new
                );
    }
}
