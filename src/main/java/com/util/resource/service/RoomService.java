package com.util.resource.service;

import com.util.dto.SingleResponseDto;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.dto.EmployeeDto;
import com.util.resource.entity.Room;
import com.util.resource.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final AuthFeignClient authFeignClient;

    public RoomService(RoomRepository roomRepository,
                       AuthFeignClient authFeignClient) {
        this.roomRepository = roomRepository;
        this.authFeignClient = authFeignClient;
    }

    public Room createRoom(Room room, long employeeId) throws IllegalArgumentException {
        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto.getData().getEmployeeId() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        if (!employeeDto.getData().getName().equals("관리자")) {
            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Room room, long roomId, long employeeId, List<String> equipmentsToDelete) {
        Room findRoom = findVerifiedRoom(roomId);

        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto.getData().getEmployeeId() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        if (!employeeDto.getData().getName().equals("관리자")) {
            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
        }

        Optional.ofNullable(room.getName())
                .ifPresent(name -> findRoom.setName(name));
        Optional.ofNullable(room.getMaxCapacity())
                .ifPresent(maxCapacity -> findRoom.setMaxCapacity(maxCapacity));
        Optional.ofNullable(room.getLocation())
                .ifPresent(location -> findRoom.setLocation(location));
        Optional.ofNullable(room.isAvailable())
                .ifPresent(available -> findRoom.setAvailable(available));

        Optional.ofNullable(room.getEquipment())
                .ifPresent(equipments -> findRoom.getEquipment().addAll(equipments));
        Optional.ofNullable(equipmentsToDelete)
                .ifPresent(toDelete -> toDelete.forEach(equipment -> findRoom.getEquipment().remove(equipment)));

        return roomRepository.save(findRoom);
    }

    public Room findRoom(long roomId) {
        Room findRoom = findVerifiedRoom(roomId);
        return findRoom;
    }

    @Transactional(readOnly = true)
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms() {
        return roomRepository.findByAvailable(true);
    }

    public void deleteRoom(long roomId, long employeeId) {
        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto.getData().getEmployeeId() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        if (!employeeDto.getData().getName().equals("관리자")) {
            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
        }
        Room findRoom = findVerifiedRoom(roomId);

        roomRepository.delete(findRoom);
    }

    public Room findVerifiedRoom(long roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        return optionalRoom.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ROOM_NOT_FOUND));
    }
}
