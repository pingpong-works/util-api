package com.util.book.service;

import com.util.book.entity.CarBook;
import com.util.book.repository.CarBookRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.EmployeeFeignClient;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Setter
public class CarBookService {
    private final CarBookRepository carBookRepository;
    private final EmployeeFeignClient employeeFeignClient;

    public CarBookService(CarBookRepository carBookRepository,
                          EmployeeFeignClient employeeFeignClient) {
        this.carBookRepository = carBookRepository;
        this.employeeFeignClient = employeeFeignClient;
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

        // employee없이 사용하는 일반 코드, 현재 car_book에는 username이 null로 저장됨
        boolean isOverlapping = carBookRepository.existsOverlappingBooking(
                carBook.getCar().getCarId(), carBook.getBookStart(), carBook.getBookEnd());
        if (isOverlapping) {
            throw new BusinessLogicException(ExceptionCode.BOOK_CONFLICT_BOOKING);
        }

        return carBookRepository.save(carBook);
    }

    public CarBook updateCarBook(CarBook carBook, long carBookId, long employeeId) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);

        if (findCarBook.getEmployeeId() != employeeId) {
            throw new BusinessLogicException(ExceptionCode.CAR_BOOK_UNAUTHORIZED_ACTION);
        }

        if (carBook.getBookStart() != null && carBook.getBookEnd() != null) {
            boolean isOverlapping = carBookRepository.existsOverlappingBooking(
                    findCarBook.getCar().getCarId(),
                    carBook.getBookStart(),
                    carBook.getBookEnd()
            );
            if (isOverlapping) {
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

        return carBookRepository.save(findCarBook);
    }

    public CarBook findCarBook(long carBookId) {
        CarBook findCarBook = findVerifiedCarBook(carBookId);
        return findCarBook;
    }

    @Transactional(readOnly = true)
    public List<CarBook> findAllCarBooks() {
        return carBookRepository.findAll();
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
}
