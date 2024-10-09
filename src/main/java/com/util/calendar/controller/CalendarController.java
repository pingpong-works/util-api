package com.util.calendar.controller;

import com.util.book.entity.CarBook;
import com.util.book.entity.RoomBook;
import com.util.book.service.CarBookService;
import com.util.book.service.RoomBookService;
import com.util.calendar.dto.CalendarDto;
import com.util.calendar.entity.Calendar;
import com.util.calendar.mapper.CalendarMapper;
import com.util.calendar.service.CalendarService;
import com.util.dto.SingleResponseDto;
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
@RequestMapping("/books/{book-id}/calendars")
public class CalendarController {
    private final static String CALENDAR_DEFAULT_URL = "/books/{book-id}/calendars";
    private final CalendarService calendarService;
    private final CarBookService carBookService;
    private final RoomBookService roomBookService;
    private final CalendarMapper mapper;

    public CalendarController(CalendarService calendarService,
                              CarBookService carBookService,
                              RoomBookService roomBookService,
                              CalendarMapper mapper) {
        this.calendarService = calendarService;
        this.carBookService = carBookService;
        this.roomBookService = roomBookService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postCalendar(@PathVariable("book-id") @Positive long bookId,
                                       @Valid @RequestBody CalendarDto.Post requestBody,
                                       @RequestParam("departmentId") @Positive long departmentId) {
        Calendar calendar = mapper.calendarPostDtoToCalendar(requestBody);

        if (calendar.getBook() == Calendar.bookType.Car) {
            CarBook carBook = carBookService.findVerifiedCarBook(bookId);
            calendar.setCarBook(carBook);
        } else if (calendar.getBook() == Calendar.bookType.Room) {
            RoomBook roomBook = roomBookService.findVerifiedroomBook(bookId);
            calendar.setRoomBook(roomBook);
        }

        Calendar createCalendar = calendarService.createCalendar(calendar, departmentId);

        URI location = UriCreator.createUri(CALENDAR_DEFAULT_URL.replace("{book-id}", String.valueOf(bookId)), createCalendar.getCalendarId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{calendar-id}")
    public ResponseEntity patchCalendar(@PathVariable("calendar-id") @Positive long calendarId,
                                        @Valid @RequestBody CalendarDto.Patch requestBody,
                                        @RequestParam("departmentId") @Positive long departmentId) {
        Calendar updateCalendar = mapper.calendarPatchDtoToCalendar(requestBody);
        Calendar calendar = calendarService.updateCalendar(updateCalendar, calendarId, departmentId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.calendarToCalendarResponseDto(calendar)), HttpStatus.OK);
    }

    @GetMapping("/{calendar-id}")
    public ResponseEntity getCalendar(@PathVariable("calendar-id") @Positive long calendarId) {
        Calendar calendar = calendarService.findCalendar(calendarId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.calendarToCalendarResponseDto(calendar)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CalendarDto.Response>> getCalendars() {
        List<Calendar> calendars = calendarService.findAllCalendar();
        List<CalendarDto.Response> response = mapper.calendarToCalendarResponseDtos(calendars);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{calendar-id}")
    public ResponseEntity deleteCalendar(@PathVariable("calendar-id") @Positive long calendarId,
                                         @RequestParam("departmentId") @Positive long departmentId) {
        calendarService.deleteCalendar(calendarId, departmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
