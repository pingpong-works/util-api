package com.util.book.controller;

import com.util.book.dto.CarBookDto;
import com.util.book.entity.CarBook;
import com.util.book.mapper.CarBookMapper;
import com.util.book.service.CarBookService;
import com.util.calendar.entity.Calendar;
import com.util.calendar.service.CalendarService;
import com.util.dto.SingleResponseDto;
import com.util.feign.AuthFeignClient;
import com.util.feign.UserResponse;
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
@RequestMapping
public class CarBookController {
    private final static String CAR_BOOK_DEFAULT_URL = "/cars/{car-id}/books";
    private final CarBookService carBookService;
    private final CarService carService;
    private final CarBookMapper mapper;
    private final CalendarService calendarService;
    private final AuthFeignClient authFeignClient;

    public CarBookController(CarBookService carBookService,
                             CarService carService,
                             CarBookMapper mapper,
                             CalendarService calendarService,
                             AuthFeignClient authFeignClient) {
        this.carBookService = carBookService;
        this.carService = carService;
        this.mapper = mapper;
        this.calendarService = calendarService;
        this.authFeignClient = authFeignClient;
    }

    @PostMapping("/cars/{car-id}/books")
    public ResponseEntity postCarBook(@PathVariable("car-id") @Positive long carId,
                                      @Valid @RequestBody CarBookDto.Post requestBody,
                                      @RequestParam("employeeId") @Positive long employeeId,
                                      @RequestParam(value = "title", required = true) String title,
                                      @RequestParam(value = "content", required = true) String content) {
        CarBook carBook = mapper.carBookPostDtoToCarBook(requestBody);

        Car car = carService.findVerifiedCar(carId);
        carBook.setCar(car);

        CarBook createCarBook = carBookService.createCarBook(carBook, employeeId);

        Calendar calendar = new Calendar();
        calendar.setTitle(title);
        calendar.setContent(content);
        calendar.setStartTime(requestBody.getBookStart());
        calendar.setEndTime(requestBody.getBookEnd());
        UserResponse employeeDto = authFeignClient.getEmployeeById(employeeId);
        calendar.setDepartmentId(employeeDto.getData().getDepartmentId());

        calendar.setCarBook(createCarBook);
        createCarBook.setCalendar(calendar);

        calendarService.createCalendar(calendar, employeeId);

        URI location = UriCreator.createUri(CAR_BOOK_DEFAULT_URL.replace("{car-id}", String.valueOf(carId)), createCarBook.getCarBookId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/car-books/{book-id}")
    public ResponseEntity patchCarBook(@PathVariable("book-id") @Positive long carBookId,
                                       @Valid @RequestBody CarBookDto.Patch requestBody,
                                       @RequestParam("departmentId") @Positive long departmentId,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "content", required = false) String content) {
        CarBook updateCarBook = mapper.carBookPatchDtoToCarBook(requestBody);
        CarBook carBook = carBookService.updateCarBook(updateCarBook, carBookId, departmentId, title, content);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.carBookToCarBookResponseDto(carBook)), HttpStatus.OK);
    }

    @GetMapping("/car-books/{book-id}")
    public ResponseEntity getCarBook(@PathVariable("book-id") @Positive long carBookId) {
        CarBook carBook = carBookService.findCarBook(carBookId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.carBookToCarBookResponseDto(carBook)), HttpStatus.OK);
    }

    @GetMapping("/car-books")
    public ResponseEntity<List<CarBookDto.Response>> getCarBooks(@RequestParam("carId") @Positive long carId) {
        List<CarBook> carBooks = carBookService.findAllByCarId(carId);
        List<CarBookDto.Response> response = mapper.carBooksToCarBookResponseDtos(carBooks);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/car-books/{book-id}")
    public ResponseEntity deleteCarBook(@PathVariable("book-id") @Positive long carBookId,
                                        @RequestParam("departmentId") @Positive long departmentId) {
        carBookService.deleteCarBook(carBookId, departmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
