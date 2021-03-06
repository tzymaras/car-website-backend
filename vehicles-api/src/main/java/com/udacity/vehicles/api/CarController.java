package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.*;
import io.swagger.annotations.*;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     *
     * @return list of vehicles
     */
    @GetMapping
    Resources<Resource<Car>> list() {
        List<Resource<Car>> resources = this.carService.list()
                .stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(resources, linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    /**
     * Gets information of a specific car by ID.
     *
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = CarNotFoundException.CAR_NOT_FOUND_MESSAGE)
    })
    @GetMapping("/{id}")
    Resource<Car> get(@PathVariable Long id) {
        return assembler.toResource(
                this.carService.findById(id)
        );
    }

    /**
     * Posts information to create a new vehicle in the system.
     *
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = ManufacturerNotExistsException.NOT_EXISTS_MESSAGE)
    })
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {
        Resource<Car> resource = assembler.toResource(
                this.carService.save(car)
        );

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     *
     * @param id  The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = CarNotFoundException.CAR_NOT_FOUND_MESSAGE),
        @ApiResponse(code = 400, message = ManufacturerNotExistsException.NOT_EXISTS_MESSAGE)
    })
    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        car.setId(id);

        Resource<Car> storedCarAsResource = assembler.toResource(
                this.carService.save(car)
        );

        return ResponseEntity.ok(storedCarAsResource);
    }

    /**
     * Removes a vehicle from the system.
     *
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = CarNotFoundException.CAR_NOT_FOUND_MESSAGE),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        this.carService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
