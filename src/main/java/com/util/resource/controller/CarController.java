package com.util.resource.controller;

import com.util.dto.SingleResponseDto;
import com.util.resource.dto.CarDto;
import com.util.resource.entity.Car;
import com.util.resource.mapper.CarMapper;
import com.util.resource.service.CarService;
import com.util.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/cars")
public class CarController {
    private final static String CAR_DEFAULT_URL = "/cars";
    private final CarService carService;
    private final CarMapper mapper;

    public CarController(CarService carService, CarMapper mapper) {
        this.carService = carService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postCar(@Valid @RequestBody CarDto.Post requestBody,
                                  @RequestParam("employeeId") @Positive long employeeId) {
        Car car = mapper.carPostDtoToCar(requestBody);
        Car createCar = carService.createCar(car, employeeId);
        URI location = UriCreator.createUri(CAR_DEFAULT_URL, createCar.getCarId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{car-id}")
    public ResponseEntity patchCar(@PathVariable("car-id") @Positive long carId,
                                   @Valid @RequestBody CarDto.Patch requestBody,
                                   @RequestParam("employeeId") @Positive long employeeId) {
        Car car = mapper.carPatchDtoToCar(requestBody);

        Car updateCar = carService.updateCar(car, carId, employeeId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.carToCarResponseDto(updateCar)), HttpStatus.OK);
    }

    @GetMapping("/{car-id}")
    public ResponseEntity getCar(@PathVariable("car-id") @Positive long carId) {
        Car car = carService.findCar(carId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.carToCarResponseDto(car)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CarDto.Response>> getCars() {
        List<Car> cars = carService.findAllCars();
        List<CarDto.Response> response = mapper.carsToCarResponseDtos(cars);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<CarDto.Response>> getAvailableCars() {
        List<Car> availableCars = carService.findAvailableCars();
        List<CarDto.Response> response = mapper.carsToCarResponseDtos(availableCars);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{car-id}")
    public ResponseEntity deleteCar(@PathVariable("car-id") @Positive long carId,
                                    @RequestParam("employeeId") @Positive long employeeId) {
        carService.deleteCar(carId, employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
