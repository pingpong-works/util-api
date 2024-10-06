package com.util.book.controller;

import com.util.book.dto.RoomBookDto;
import com.util.book.entity.RoomBook;
import com.util.book.mapper.RoomBookMapper;
import com.util.book.service.RoomBookService;
import com.util.dto.SingleResponseDto;
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

@RestController
@RequestMapping("/rooms/{room-id}/books")
public class RoomBookController {
    private final static String ROOM_BOOK_DEFAULT_URL = "/rooms/{room-id}/books";
    private final RoomBookService roomBookService;
    private final RoomService roomService;
    private final RoomBookMapper mapper;

    public RoomBookController(RoomBookService roomBookService,
                             RoomService roomService,
                             RoomBookMapper mapper) {
        this.roomBookService = roomBookService;
        this.roomService = roomService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postRoomBook(@PathVariable("room-id") @Positive long roomId,
                                      @Valid @RequestBody RoomBookDto.Post requestBody,
                                      @RequestParam("employeeId") @Positive long employeeId) {
        RoomBook roomBook = mapper.roomBookPostDtoToRoomBook(requestBody);

        Room room = roomService.findVerifiedRoom(roomId);
        roomBook.setRoom(room);

        RoomBook createRoomBook = roomBookService.createRoomBook(roomBook, employeeId);

        URI location = UriCreator.createUri(ROOM_BOOK_DEFAULT_URL.replace("{room-id}", String.valueOf(roomId)), createRoomBook.getRoomBookId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{book-id}")
    public ResponseEntity patchRoomBook(@PathVariable("book-id") @Positive long roomBookId,
                                       @Valid @RequestBody RoomBookDto.Patch requestBody,
                                       @RequestParam("employeeId") @Positive long employeeId) {
        RoomBook updateRoomBook = mapper.roomBookPatchDtoToRoomBook(requestBody);
        RoomBook roomBook = roomBookService.updateRoomBook(updateRoomBook, roomBookId, employeeId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomBookToRoomBookResponseDto(roomBook)), HttpStatus.OK);
    }

    @GetMapping("/{book-id}")
    public ResponseEntity getRoomBook(@PathVariable("book-id") @Positive long roomBookId) {
        RoomBook roomBook = roomBookService.findRoomBook(roomBookId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomBookToRoomBookResponseDto(roomBook)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RoomBookDto.Response>> getRoomBooks(@PathVariable("room-id") @Positive long roomId) {
        List<RoomBook> roomBooks = roomBookService.findAllByRoomId(roomId);
        List<RoomBookDto.Response> response = mapper.roomBooksToRoomBookResponseDtos(roomBooks);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{book-id}")
    public ResponseEntity deleteRoomBook(@PathVariable("book-id") @Positive long roomBookId,
                                        @RequestParam("employeeId") @Positive long employeeId) {
        roomBookService.deleteRoomBook(roomBookId, employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
