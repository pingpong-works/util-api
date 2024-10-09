package com.util.book.controller;

import com.util.book.dto.CarBookDto;
import com.util.book.entity.CarBook;
import com.util.book.mapper.CarBookMapper;
import com.util.book.service.CarBookService;
import com.util.dto.SingleResponseDto;
import com.util.resource.entity.Car;
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
@RequestMapping("/cars/{car-id}/books")
public class CarBookController {
    private final static String CAR_BOOK_DEFAULT_URL = "/cars/{car-id}/books";
    private final CarBookService carBookService;
    private final CarService carService;
    private final CarBookMapper mapper;

    public CarBookController(CarBookService carBookService,
                             CarService carService,
                             CarBookMapper mapper) {
        this.carBookService = carBookService;
        this.carService = carService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postCarBook(@PathVariable("car-id") @Positive long carId,
                                      @Valid @RequestBody CarBookDto.Post requestBody,
                                      @RequestParam("employeeId") @Positive long employeeId) {
        CarBook carBook = mapper.carBookPostDtoToCarBook(requestBody);

        Car car = carService.findVerifiedCar(carId);
        carBook.setCar(car);

        CarBook createCarBook = carBookService.createCarBook(carBook, employeeId);

        URI location = UriCreator.createUri(CAR_BOOK_DEFAULT_URL.replace("{car-id}", String.valueOf(carId)), createCarBook.getCarBookId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{book-id}")
    public ResponseEntity patchCarBook(@PathVariable("book-id") @Positive long carBookId,
                                       @Valid @RequestBody CarBookDto.Patch requestBody,
                                       @RequestParam("employeeId") @Positive long employeeId) {
        CarBook updateCarBook = mapper.carBookPatchDtoToCarBook(requestBody);
        CarBook carBook = carBookService.updateCarBook(updateCarBook, carBookId, employeeId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.carBookToCarBookResponseDto(carBook)), HttpStatus.OK);
    }

    @GetMapping("/{book-id}")
    public ResponseEntity getCarBook(@PathVariable("book-id") @Positive long carBookId) {
        CarBook carBook = carBookService.findCarBook(carBookId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.carBookToCarBookResponseDto(carBook)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CarBookDto.Response>> getCarBooks(@PathVariable("car-id") @Positive long carId) {
        List<CarBook> carBooks = carBookService.findAllByCarId(carId);
        List<CarBookDto.Response> response = mapper.carBooksToCarBookResponseDtos(carBooks);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{book-id}")
    public ResponseEntity deleteCarBook(@PathVariable("book-id") @Positive long carBookId,
                                        @RequestParam("employeeId") @Positive long employeeId) {
        carBookService.deleteCarBook(carBookId, employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
