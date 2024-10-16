package com.util.book.service;

import com.alarm.kafka.UtilProducer;
import com.util.book.entity.RoomBook;
import com.util.book.repository.RoomBookRepository;
import com.util.calendar.repository.CalendarRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.UserResponse;
import com.util.resource.entity.Room;
import com.util.resource.repository.RoomRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class RoomBookService {
    private final RoomBookRepository roomBookRepository;
    private final AuthFeignClient authFeignClient;
    private final UtilProducer utilProducer;
    private final CalendarRepository calendarRepository;
    private final RoomRepository roomRepository;

    public RoomBookService(RoomBookRepository roomBookRepository,
                           AuthFeignClient authFeignClient, UtilProducer utilProducer, CalendarRepository calendarRepository, RoomRepository roomRepository) {
        this.roomBookRepository = roomBookRepository;
        this.authFeignClient = authFeignClient;
        this.utilProducer = utilProducer;
        this.calendarRepository = calendarRepository;
        this.roomRepository = roomRepository;
    }

    public RoomBook createRoomBook(RoomBook roomBook, long employeeId) throws IllegalArgumentException {
        UserResponse employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto != null && employeeDto.getData().getEmployeeId() != null) {
            Long fetchEmployeeId = employeeDto.getData().getEmployeeId();
            String employeeName = employeeDto.getData().getName();

            roomBook.setEmployeeId(fetchEmployeeId);
            roomBook.setEmployeeName(employeeName);

            boolean isBooking = roomBookRepository.existsOverlappingBooking(
                    roomBook.getRoom().getRoomId(), roomBook.getBookStart(), roomBook.getBookEnd());
            if (isBooking) {
                throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
            }

            RoomBook savedRoomBook = roomBookRepository.save(roomBook);
            sendRoomBookAlarm(savedRoomBook, savedRoomBook.getStatus().getStatus());

            return savedRoomBook;
        }
        else {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }
    }

    public RoomBook updateRoomBook(RoomBook roomBook, long roomBookId, long departmentId, String title, String content) {
        RoomBook findRoomBook = findVerifiedroomBook(roomBookId);

        UserResponse employeeDto = authFeignClient.getEmployeeById(findRoomBook.getEmployeeId());

        if (employeeDto.getData().getDepartmentId() != departmentId) {
            throw new BusinessLogicException(ExceptionCode.CAR_BOOK_UNAUTHORIZED_ACTION);
        }


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

        Optional.ofNullable(title)
                .ifPresent(t -> findRoomBook.getCalendar().setTitle(t));
        Optional.ofNullable(content)
                .ifPresent(c -> findRoomBook.getCalendar().setContent(c));
        Optional.ofNullable(roomBook.getRoom().getRoomId())
                .ifPresent(newRoomId -> {
                    Room newRoom = roomRepository.findById(newRoomId)
                            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
                    findRoomBook.setRoom(newRoom);
                });
        Optional.ofNullable(roomBook.getBookStart())
                .ifPresent(bookStart -> findRoomBook.setBookStart(bookStart));
        Optional.ofNullable(roomBook.getBookStart())
                .ifPresent(bookStart -> findRoomBook.getCalendar().setStartTime(bookStart));
        Optional.ofNullable(roomBook.getBookEnd())
                .ifPresent(bookEnd -> findRoomBook.setBookEnd(bookEnd));
        Optional.ofNullable(roomBook.getBookEnd())
                .ifPresent(bookEnd -> findRoomBook.getCalendar().setEndTime(bookEnd));
        Optional.ofNullable(roomBook.getPurpose())
                .ifPresent(purpose -> findRoomBook.setPurpose(purpose));
        Optional.ofNullable(roomBook.getStatus())
                .ifPresent(status -> findRoomBook.setStatus(status));

        RoomBook savedRoomBook = roomBookRepository.save(findRoomBook);
        calendarRepository.save(findRoomBook.getCalendar());

        //예약 상태 변경 시 알림 발송
        if(savedRoomBook.getStatus().equals(RoomBook.StatusType.CANCELLED)
                || savedRoomBook.getStatus().equals(RoomBook.StatusType.CONFIRMED)) {

            sendRoomBookAlarm(savedRoomBook, savedRoomBook.getStatus().getStatus());
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

    public void deleteRoomBook(long roomBookId, long departmentId) {
        RoomBook findRoomBook = findVerifiedroomBook(roomBookId);

        UserResponse employeeDto = authFeignClient.getEmployeeById(findRoomBook.getEmployeeId());

        if (employeeDto.getData().getDepartmentId() == departmentId) {
            roomBookRepository.delete(findRoomBook);
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
                String.format("회의실[%s]의 예약이 [%s]되었습니다.", roomBook.getRoom().getName(), status),
                roomBook.getRoomBookId());
    }
}
