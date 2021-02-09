package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.*;
import com.udacity.vehicles.domain.car.*;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;

import java.net.URI;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);

        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        Car car = getCar();

        mvc.perform(
                get(new URI("/cars"))
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(
                        ResultMatcher.matchAll(
                                status().isOk(),
                                jsonPath("$._embedded.carList[0].condition").value(car.getCondition().toString()),
                                jsonPath("$._embedded.carList[0].location.lat").value(car.getLocation().getLat()),
                                jsonPath("$._embedded.carList[0].location.lon").value(car.getLocation().getLon()),
                                jsonPath("$._embedded.carList[0].details.body").value(car.getDetails().getBody()),
                                jsonPath("$._embedded.carList[0].details.model").value(car.getDetails().getModel()),
                                jsonPath("$._embedded.carList[0].details.numberOfDoors").value(car.getDetails().getNumberOfDoors()),
                                jsonPath("$._embedded.carList[0].details.fuelType").value(car.getDetails().getFuelType()),
                                jsonPath("$._embedded.carList[0].details.engine").value(car.getDetails().getEngine()),
                                jsonPath("$._embedded.carList[0].details.mileage").value(car.getDetails().getMileage()),
                                jsonPath("$._embedded.carList[0].details.modelYear").value(car.getDetails().getModelYear()),
                                jsonPath("$._embedded.carList[0].details.productionYear").value(car.getDetails().getProductionYear()),
                                jsonPath("$._embedded.carList[0].details.externalColor").value(car.getDetails().getExternalColor()),
                                jsonPath("$._embedded.carList[0].details.manufacturer.code").value(car.getDetails().getManufacturer().getCode()),
                                jsonPath("$._embedded.carList[0].details.manufacturer.name").value(car.getDetails().getManufacturer().getName())
                        ));
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        Car car = this.getCar();

        this.mvc.perform(get(new URI("/cars/1"))
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(ResultMatcher.matchAll(
                        jsonPath("$.condition").value(car.getCondition().toString()),
                        jsonPath("$.location.lat").value(car.getLocation().getLat()),
                        jsonPath("$.location.lon").value(car.getLocation().getLon()),
                        jsonPath("$.details.body").value(car.getDetails().getBody()),
                        jsonPath("$.details.model").value(car.getDetails().getModel()),
                        jsonPath("$.details.numberOfDoors").value(car.getDetails().getNumberOfDoors()),
                        jsonPath("$.details.fuelType").value(car.getDetails().getFuelType()),
                        jsonPath("$.details.engine").value(car.getDetails().getEngine()),
                        jsonPath("$.details.mileage").value(car.getDetails().getMileage()),
                        jsonPath("$.details.modelYear").value(car.getDetails().getModelYear()),
                        jsonPath("$.details.productionYear").value(car.getDetails().getProductionYear()),
                        jsonPath("$.details.externalColor").value(car.getDetails().getExternalColor()),
                        jsonPath("$.details.manufacturer.code").value(car.getDetails().getManufacturer().getCode()),
                        jsonPath("$.details.manufacturer.name").value(car.getDetails().getManufacturer().getName())
                ));
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        this.mvc.perform(delete(new URI("/cars/1")))
                .andExpect(status().isNoContent());
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);

        return car;
    }
}