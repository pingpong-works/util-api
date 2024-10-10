package com.util.book.service;

import com.alarm.kafka.UtilProducer;
import com.util.book.entity.CarBook;
import com.util.book.repository.CarBookRepository;
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
public class CarBookService {
    private final CarBookRepository carBookRepository;
    private final EmployeeFeignClient employeeFeignClient;
    private final UtilProducer utilProducer;

    public CarBookService(CarBookRepository carBookRepository,
                          EmployeeFeignClient employeeFeignClient, UtilProducer utilProducer) {
        this.carBookRepository = carBookRepository;
        this.employeeFeignClient = employeeFeignClient;
        this.utilProducer = utilProducer;
    }

    public CarBook createCarBook(CarBook carBook, long employeeId) throws IllegalArgumentException {
        // employee 호출할 경우 사용하는 코드
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);
//
//        if (employee.containsKey("employeeId")) {
//            Long fetchEmployeeId = (Long) employee.get("employeeId");
//            String employeeName = (String) employee.get("username");
//
//            carBook.setEmployeeId(fetchEmployeeId);
//            carBook.setEmployeeName(employeeName);
//
//        boolean isBooking = carBookRepository.existsOverlappingBooking(
//                carBook.getCar().getCarId(), carBook.getBookStart(), carBook.getBookEnd());
//        if (isBooking) {
//            throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
//        }

//            return carBookRepository.save(carBook);
//        }
//        else {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }

        // employee없이 사용하는 일반 코드, 현재 carBook에는 username이 null로 저장됨
        carBook.setEmployeeId(employeeId);
        boolean isOverlapping = carBookRepository.existsOverlappingBooking(
                carBook.getCar().getCarId(), carBook.getBookStart(), carBook.getBookEnd());
        if (isOverlapping) {
            throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
        }

        CarBook savedCarBook =  carBookRepository.save(carBook);
        sendCarBookAlarm(savedCarBook, "신청");

        return savedCarBook;
    }

    public CarBook updateCarBook(CarBook carBook, long carBookId, long employeeId) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);

        LocalDateTime originalBookStart = findCarBook.getBookStart();
        LocalDateTime originalBookEnd = findCarBook.getBookEnd();

        if (carBook.getBookStart() != null && carBook.getBookEnd() != null) {
            // 일단 DB에서 기존 시간을 제외한 상태로 중복 예약 체크 수행
            findCarBook.setBookStart(null);
            findCarBook.setBookEnd(null);
            carBookRepository.save(findCarBook); // 중복 예약 검사 전에 기존 시간을 일시적으로 제거

            boolean isOverlapping = carBookRepository.existsOverlappingBooking(
                    findCarBook.getCar().getCarId(),
                    carBook.getBookStart(),
                    carBook.getBookEnd()
            );

            // 중복이 발생한 경우, 원래 시간을 복원하고 예외 처리
            if (isOverlapping) {
                findCarBook.setBookStart(originalBookStart);
                findCarBook.setBookEnd(originalBookEnd);
                throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
            }
        }

        Optional.ofNullable(carBook.getBookStart())
                .ifPresent(bookStart -> findCarBook.setBookStart(bookStart));
        Optional.ofNullable(carBook.getBookEnd())
                .ifPresent(bookEnd -> findCarBook.setBookEnd(bookEnd));
        Optional.ofNullable(carBook.getPurpose())
                .ifPresent(purpose -> findCarBook.setPurpose(purpose));
        Optional.ofNullable(carBook.getStatus())
                .ifPresent(status -> findCarBook.setStatus(status));

        CarBook savedCarBook =  carBookRepository.save(findCarBook);


        //예약 상태 변경 시 알림 발송
        if(savedCarBook.getStatus().equals(CarBook.StatusType.CANCELLED)
                || savedCarBook.getStatus().equals(CarBook.StatusType.CONFIRMED)) {

            sendCarBookAlarm(savedCarBook, savedCarBook.getStatus().getStatus().toString());
        }

        return  savedCarBook;
    }

    public CarBook findCarBook(long carBookId) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);
        return findCarBook;
    }

    @Transactional(readOnly = true)
    public List<CarBook> findAllByCarId(long carId) {
        return carBookRepository.findAllByCar_CarId(carId);
    }

    public void deleteCarBook(long carBookId, long employeeId) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);

        if (findCarBook.getEmployeeId() == employeeId) {
            carBookRepository.delete(findCarBook);
        }else {
            throw new BusinessLogicException(ExceptionCode.CAR_BOOK_UNAUTHORIZED_ACTION);
        }
    }

    public CarBook findVerifiedCarBook(long carBookId) {
        Optional<CarBook> optionalCarBook = carBookRepository.findById(carBookId);
        return optionalCarBook.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.CAR_BOOK_NOT_FOUND));
    }

    private void sendCarBookAlarm(CarBook carBook, String status) {

        utilProducer.sendBookCarNotification(carBook.getEmployeeId(),
                String.format("차량[%s]의 예약[%s]이 완료되었습니다.", carBook.getCar().getNumber(), status),
                carBook.getCarBookId());
    }
}
