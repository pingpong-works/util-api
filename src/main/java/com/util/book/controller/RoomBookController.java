package com.util.book.controller;

import com.util.book.dto.RoomBookDto;
import com.util.book.entity.RoomBook;
import com.util.book.mapper.RoomBookMapper;
import com.util.book.service.RoomBookService;
import com.util.calendar.entity.Calendar;
import com.util.calendar.service.CalendarService;
import com.util.dto.SingleResponseDto;
import com.util.feign.AuthFeignClient;
import com.util.feign.dto.EmployeeDto;
import com.util.resource.entity.Room;
import com.util.resource.service.RoomService;
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
public class RoomBookController {
    private final static String ROOM_BOOK_DEFAULT_URL = "/rooms/{room-id}/books";
    private final RoomBookService roomBookService;
    private final RoomService roomService;
    private final RoomBookMapper mapper;
    private final CalendarService calendarService;
    private final AuthFeignClient authFeignClient;

    public RoomBookController(RoomBookService roomBookService,
                              RoomService roomService,
                              RoomBookMapper mapper,
                              CalendarService calendarService,
                              AuthFeignClient authFeignClient) {
        this.roomBookService = roomBookService;
        this.roomService = roomService;
        this.mapper = mapper;
        this.calendarService = calendarService;
        this.authFeignClient = authFeignClient;
    }

    @PostMapping("/rooms/{room-id}/books")
    public ResponseEntity postRoomBook(@PathVariable("room-id") @Positive long roomId,
                                       @Valid @RequestBody RoomBookDto.Post requestBody,
                                       @RequestParam("employeeId") @Positive long employeeId,
                                       @RequestParam(value = "title", required = true) String title,
                                       @RequestParam(value = "content", required = true) String content) {
        RoomBook roomBook = mapper.roomBookPostDtoToRoomBook(requestBody);

        Room room = roomService.findVerifiedRoom(roomId);
        roomBook.setRoom(room);

        RoomBook createRoomBook = roomBookService.createRoomBook(roomBook, employeeId);

        Calendar calendar = new Calendar();
        calendar.setTitle(title);
        calendar.setContent(content);
        calendar.setStartTime(requestBody.getBookStart());
        calendar.setEndTime(requestBody.getBookEnd());
        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);
        calendar.setDepartmentId(employeeDto.getData().getDepartmentId());

        calendar.setRoomBook(createRoomBook);
        createRoomBook.setCalendar(calendar);

        calendarService.createCalendar(calendar, employeeId);

        URI location = UriCreator.createUri(ROOM_BOOK_DEFAULT_URL.replace("{room-id}", String.valueOf(roomId)), createRoomBook.getRoomBookId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/room-books/{book-id}")
    public ResponseEntity patchRoomBook(@PathVariable("book-id") @Positive long roomBookId,
                                       @Valid @RequestBody RoomBookDto.Patch requestBody,
                                       @RequestParam("departmentId") @Positive long departmentId) {
        RoomBook updateRoomBook = mapper.roomBookPatchDtoToRoomBook(requestBody);
        RoomBook roomBook = roomBookService.updateRoomBook(updateRoomBook, roomBookId, departmentId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomBookToRoomBookResponseDto(roomBook)), HttpStatus.OK);
    }

    @GetMapping("/room-books/{book-id}")
    public ResponseEntity getRoomBook(@PathVariable("book-id") @Positive long roomBookId) {
        RoomBook roomBook = roomBookService.findRoomBook(roomBookId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomBookToRoomBookResponseDto(roomBook)), HttpStatus.OK);
    }

    @GetMapping("/room-books")
    public ResponseEntity<List<RoomBookDto.Response>> getRoomBooks(@RequestParam("roomId") @Positive long roomId) {
        List<RoomBook> roomBooks = roomBookService.findAllByRoomId(roomId);
        List<RoomBookDto.Response> response = mapper.roomBooksToRoomBookResponseDtos(roomBooks);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/room-books/{book-id}")
    public ResponseEntity deleteRoomBook(@PathVariable("book-id") @Positive long roomBookId,
                                        @RequestParam("departmentId") @Positive long departmentId) {
        roomBookService.deleteRoomBook(roomBookId, departmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
