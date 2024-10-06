package com.util.book.service;


import com.util.book.entity.RoomBook;
import com.util.book.repository.RoomBookRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.EmployeeFeignClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoomBookService {
    private final RoomBookRepository roomBookRepository;
    private final EmployeeFeignClient employeeFeignClient;

    public RoomBookService(RoomBookRepository roomBookRepository,
                          EmployeeFeignClient employeeFeignClient) {
        this.roomBookRepository = roomBookRepository;
        this.employeeFeignClient = employeeFeignClient;
    }

    public RoomBook createroomBook(RoomBook roomBook, long employeeId) throws IllegalArgumentException {
        // employee 호출할 경우 사용하는 코드
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);
//
//        if (employee.containsKey("employeeId")) {
//            Long fetchEmployeeId = (Long) employee.get("employeeId");
//            String employeeName = (String) employee.get("username");
//
//            roomBook.setEmployeeId(fetchEmployeeId);
//            roomBook.setEmployeeName(employeeName);
//
//        boolean isBooking = roomBookRepository.existsOverlappingBooking(
//                roomBook.getRoom().getRoomId(), roomBook.getBookStart(), roomBook.getBookEnd());
//        if (isBooking) {
//            throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
//        }
//
//            return roomBookRepository.save(roomBook);
//        }
//        else {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }

        // employee없이 사용하는 일반 코드, 현재 board에는 username이 null로 저장됨
        boolean isOverlapping = roomBookRepository.existsOverlappingBooking(
                roomBook.getRoom().getRoomId(), roomBook.getBookStart(), roomBook.getBookEnd());
        if (isOverlapping) {
            throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
        }

        return roomBookRepository.save(roomBook);
    }

    public RoomBook updateroomBook(RoomBook roomBook, long roomBookId, long employeeId) {
        RoomBook findRoomBook = findVerifiedroomBook(roomBookId);

        if (findRoomBook.getEmployeeId() != employeeId) {
            throw new BusinessLogicException(ExceptionCode.ROOM_BOOK_UNAUTHORIZED_ACTION);
        }

        if (roomBook.getBookStart() != null && roomBook.getBookEnd() != null) {
            boolean isOverlapping = roomBookRepository.existsOverlappingBooking(
                    findRoomBook.getRoom().getRoomId(),
                    roomBook.getBookStart(),
                    roomBook.getBookEnd()
            );
            if (isOverlapping) {
                throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
            }
        }

        Optional.ofNullable(roomBook.getBookStart())
                .ifPresent(bookStart -> findRoomBook.setBookStart(bookStart));
        Optional.ofNullable(roomBook.getBookEnd())
                .ifPresent(bookEnd -> findRoomBook.setBookEnd(bookEnd));
        Optional.ofNullable(roomBook.getPurpose())
                .ifPresent(purpose -> findRoomBook.setPurpose(purpose));
        Optional.ofNullable(roomBook.getStatus())
                .ifPresent(status -> findRoomBook.setStatus(status));

        return roomBookRepository.save(findRoomBook);
    }

    public RoomBook findroomBook(long roomBookId) {
        RoomBook findRoomBook = findVerifiedroomBook(roomBookId);
        return findRoomBook;
    }

    @Transactional(readOnly = true)
    public List<RoomBook> findAllRoomBooks() {
        return roomBookRepository.findAll();
    }

    public void deleteRoomBook(long roomBookId, long employeeId) {
        RoomBook findroomBook = findVerifiedroomBook(roomBookId);

        if (findroomBook.getEmployeeId() == employeeId) {
            roomBookRepository.delete(findroomBook);
        }else {
            throw new BusinessLogicException(ExceptionCode.ROOM_BOOK_UNAUTHORIZED_ACTION);
        }
    }

    public RoomBook findVerifiedroomBook(long roomBookId) {
        Optional<RoomBook> optionalroomBook = roomBookRepository.findById(roomBookId);
        return optionalroomBook.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ROOM_BOOK_NOT_FOUND));
    }
}
