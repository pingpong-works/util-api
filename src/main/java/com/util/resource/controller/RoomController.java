package com.util.resource.controller;

import com.util.dto.SingleResponseDto;
import com.util.resource.dto.RoomDto;
import com.util.resource.entity.Room;
import com.util.resource.mapper.RoomMapper;
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
@RequestMapping("/rooms")
public class RoomController {
    private final static String ROOM_DEFAULT_URL = "/rooms";
    private final RoomService roomService;
    private final RoomMapper mapper;

    public RoomController(RoomService roomService, RoomMapper mapper) {
        this.roomService = roomService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postRoom(@Valid @RequestBody RoomDto.Post requestBody,
                                   @RequestParam("employeeId") @Positive long employeeId) {
        Room room = mapper.roomPostDtoToRoom(requestBody);
        Room createRoom = roomService.createRoom(room, employeeId);
        URI location = UriCreator.createUri(ROOM_DEFAULT_URL, createRoom.getRoomId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{room-id}")
    public ResponseEntity patchRoom(@PathVariable("room-id") @Positive long roomId,
                                    @Valid @RequestBody RoomDto.Patch requestBody,
                                    @RequestParam("employeeId") @Positive long employeeId) {
        Room room = mapper.roomPatchDtoToRoom(requestBody);

        List<String> equipmentsToDelete = requestBody.getEquipmentsToDelete();

        Room updateRoom = roomService.updateRoom(room, roomId, employeeId, equipmentsToDelete);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomToRoomResponseDto(updateRoom)), HttpStatus.OK);
    }

    @GetMapping("/{room-id}")
    public ResponseEntity getRoom(@PathVariable("room-id") @Positive long roomId) {
        Room room = roomService.findRoom(roomId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomToRoomResponseDto(room)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto.Response>> getRooms() {
        List<Room> rooms = roomService.findAllRooms();
        List<RoomDto.Response> response = mapper.roomsToRoomResponseDtos(rooms);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDto.Response>> getAvailableRooms() {
        List<Room> availableRooms = roomService.findAvailableRooms();
        List<RoomDto.Response> response = mapper.roomsToRoomResponseDtos(availableRooms);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{room-id}")
    public ResponseEntity deleteRoom(@PathVariable("room-id") @Positive long roomId,
                                     @RequestParam("employeeId") @Positive long employeeId) {
        roomService.deleteRoom(roomId, employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
