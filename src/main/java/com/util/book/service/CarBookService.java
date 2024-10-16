package com.util.book.service;

import com.alarm.kafka.UtilProducer;
import com.util.book.entity.CarBook;
import com.util.book.repository.CarBookRepository;
import com.util.calendar.repository.CalendarRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.UserResponse;
import com.util.resource.entity.Car;
import com.util.resource.repository.CarRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CarBookService {
    private final CarBookRepository carBookRepository;
    private final AuthFeignClient authFeignClient;
    private final UtilProducer utilProducer;
    private final CalendarRepository calendarRepository;
    private final CarRepository carRepository;

    public CarBookService(CarBookRepository carBookRepository,
                          AuthFeignClient authFeignClient, UtilProducer utilProducer, CalendarRepository calendarRepository, CarRepository carRepository) {
        this.carBookRepository = carBookRepository;
        this.authFeignClient = authFeignClient;
        this.utilProducer = utilProducer;
        this.calendarRepository = calendarRepository;
        this.carRepository = carRepository;
    }

    public CarBook createCarBook(CarBook carBook, long employeeId) throws IllegalArgumentException {
        UserResponse employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto != null && employeeDto.getData().getEmployeeId() != null) {
            Long fetchEmployeeId = employeeDto.getData().getEmployeeId();
            String employeeName = employeeDto.getData().getName();

            carBook.setEmployeeId(fetchEmployeeId);
            carBook.setEmployeeName(employeeName);

            boolean isBooking = carBookRepository.existsOverlappingBooking(
                    carBook.getCar().getCarId(), carBook.getBookStart(), carBook.getBookEnd());
            if (isBooking) {
                throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
            }

            CarBook savedCarBook =  carBookRepository.save(carBook);
            sendCarBookAlarm(savedCarBook, savedCarBook.getStatus().getStatus());

            return savedCarBook;
        }
        else {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }
    }

    public CarBook updateCarBook(CarBook carBook, long carBookId, long departmentId, String title, String content) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);

        UserResponse employeeDto = authFeignClient.getEmployeeById(findCarBook.getEmployeeId());

        if (employeeDto.getData().getDepartmentId() != departmentId) {
            throw new BusinessLogicException(ExceptionCode.CAR_BOOK_UNAUTHORIZED_ACTION);
        }

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

        Optional.ofNullable(title)
                .ifPresent(t -> findCarBook.getCalendar().setTitle(t));
        Optional.ofNullable(content)
                .ifPresent(c -> findCarBook.getCalendar().setContent(c));
        Optional.ofNullable(carBook.getCar().getCarId())
                .ifPresent(newCarId -> {
                    Car newCar = carRepository.findById(newCarId)
                            .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
                    findCarBook.setCar(newCar);
                });
        Optional.ofNullable(carBook.getBookStart())
                .ifPresent(bookStart -> findCarBook.setBookStart(bookStart));
        Optional.ofNullable(carBook.getBookStart())
                .ifPresent(bookStart -> findCarBook.getCalendar().setStartTime(bookStart));
        Optional.ofNullable(carBook.getBookEnd())
                .ifPresent(bookEnd -> findCarBook.setBookEnd(bookEnd));
        Optional.ofNullable(carBook.getBookEnd())
                .ifPresent(bookEnd -> findCarBook.getCalendar().setEndTime(bookEnd));
        Optional.ofNullable(carBook.getPurpose())
                .ifPresent(purpose -> findCarBook.setPurpose(purpose));
        Optional.ofNullable(carBook.getStatus())
                .ifPresent(status -> findCarBook.setStatus(status));

        CarBook savedCarBook =  carBookRepository.save(findCarBook);
        calendarRepository.save(findCarBook.getCalendar());


        //예약 상태 변경 시 알림 발송
        if(savedCarBook.getStatus().equals(CarBook.StatusType.CANCELLED)
                || savedCarBook.getStatus().equals(CarBook.StatusType.CONFIRMED)) {

            sendCarBookAlarm(savedCarBook, savedCarBook.getStatus().getStatus());
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

    public void deleteCarBook(long carBookId, long departmentId) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);

        UserResponse employeeDto = authFeignClient.getEmployeeById(findCarBook.getEmployeeId());

        if (employeeDto.getData().getDepartmentId() == departmentId) {
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
                String.format("차량[%s]의 예약이 [%s]되었습니다.", carBook.getCar().getNumber(), status),
                carBook.getCarBookId());
    }
}
