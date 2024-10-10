package com.util.book.service;


import com.alarm.kafka.UtilProducer;
import com.util.book.entity.CarBook;
import com.util.book.entity.RoomBook;
import com.util.book.repository.RoomBookRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.EmployeeFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class RoomBookService {
    private final RoomBookRepository roomBookRepository;
    private final EmployeeFeignClient employeeFeignClient;
    private final UtilProducer utilProducer;

    public RoomBookService(RoomBookRepository roomBookRepository,
                           EmployeeFeignClient employeeFeignClient, UtilProducer utilProducer) {
        this.roomBookRepository = roomBookRepository;
        this.employeeFeignClient = employeeFeignClient;
        this.utilProducer = utilProducer;
    }

    public RoomBook createRoomBook(RoomBook roomBook, long employeeId) throws IllegalArgumentException {
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

        // employee없이 사용하는 일반 코드, 현재 roomBook에는 username이 null로 저장됨
        roomBook.setEmployeeId(employeeId);
        boolean isOverlapping = roomBookRepository.existsOverlappingBooking(
                roomBook.getRoom().getRoomId(), roomBook.getBookStart(), roomBook.getBookEnd());
        if (isOverlapping) {
            throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
        }

        RoomBook savedRoomBook = roomBookRepository.save(roomBook);
        sendRoomBookAlarm(savedRoomBook, savedRoomBook.getStatus().getStatus().toString());

        return savedRoomBook;
    }

    public RoomBook updateRoomBook(RoomBook roomBook, long roomBookId, long employeeId) {
        RoomBook findRoomBook = findVerifiedroomBook(roomBookId);

        LocalDateTime originalBookStart = findRoomBook.getBookStart();
        LocalDateTime originalBookEnd = findRoomBook.getBookEnd();

        // 새로운 예약 시간이 있는 경우에만 중복 검사 수행
        if (roomBook.getBookStart() != null && roomBook.getBookEnd() != null) {
            // 일단 DB에서 기존 시간을 제외한 상태로 중복 예약 체크 수행
            findRoomBook.setBookStart(null);
            findRoomBook.setBookEnd(null);
            roomBookRepository.save(findRoomBook); // 중복 예약 검사 전에 기존 시간을 일시적으로 제거

            boolean isOverlapping = roomBookRepository.existsOverlappingBooking(
                    findRoomBook.getRoom().getRoomId(),
                    roomBook.getBookStart(),
                    roomBook.getBookEnd()
            );

            // 중복이 발생한 경우, 원래 시간을 복원하고 예외 처리
            if (isOverlapping) {
                findRoomBook.setBookStart(originalBookStart);
                findRoomBook.setBookEnd(originalBookEnd);
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

        RoomBook savedRoomBook = roomBookRepository.save(findRoomBook);

        //예약 상태 변경 시 알림 발송
        if(savedRoomBook.getStatus().equals(RoomBook.StatusType.CANCELLED)
                || savedRoomBook.getStatus().equals(RoomBook.StatusType.CONFIRMED)) {

            sendRoomBookAlarm(savedRoomBook, savedRoomBook.getStatus().getStatus().toString());
        }


        return savedRoomBook;
    }

    public RoomBook findRoomBook(long roomBookId) {
        RoomBook findRoomBook = findVerifiedroomBook(roomBookId);
        return findRoomBook;
    }

    @Transactional(readOnly = true)
    public List<RoomBook> findAllByRoomId(long roomId) {
        return roomBookRepository.findAllByRoom_roomId(roomId);
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

    private void sendRoomBookAlarm(RoomBook roomBook, String status) {

        utilProducer.sendBookRoomNotification(roomBook.getEmployeeId(),
                String.format("회의실[%s]의 예약[%s]이 완료되었습니다.", roomBook.getRoom().getName(), status),
                roomBook.getRoomBookId());
    }
}
