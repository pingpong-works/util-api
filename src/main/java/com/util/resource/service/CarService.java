package com.util.resource.service;

import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.EmployeeFeignClient;
import com.util.resource.entity.Car;
import com.util.resource.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CarService {
    private final CarRepository carRepository;
    private final EmployeeFeignClient employeeFeignClient;

    public CarService(CarRepository carRepository,
                      EmployeeFeignClient employeeFeignClient) {
        this.carRepository = carRepository;
        this.employeeFeignClient = employeeFeignClient;
    }

    public Car createCar(Car car, long employeeId) throws IllegalArgumentException {
        // employee 호출할 경우 사용하는 코드
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);
//
//        if (!employee.containsKey("employeeId")) {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }
//
//        if (!employee.get("username").equals("관리자")) {
//            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
//        }

        return carRepository.save(car);
    }

    public Car updateCar(Car car, long carId, long employeeId, List<String> imagesToDelete) {
        Car findCar = findVerifiedCar(carId);

        // employee 호출할 경우 사용하는 코드
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);
//
//        if (!employee.containsKey("employeeId")) {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }
//
//        if (!employee.get("username").equals("관리자")) {
//            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
//        }

        Optional.ofNullable(car.getName())
                .ifPresent(name -> findCar.setName(name));
        Optional.ofNullable(car.getNumber())
                .ifPresent(number -> findCar.setNumber(number));
        Optional.of(car.isAvailable())
                .ifPresent(available -> findCar.setAvailable(available));
        Optional.of(car.getFuel())
                .ifPresent(fuelType -> findCar.setFuel(fuelType));

        Optional.ofNullable(car.getImages())
                .ifPresent(newImages -> findCar.getImages().putAll(newImages));
        Optional.ofNullable(imagesToDelete)
                .ifPresent(toDelete -> toDelete.forEach(url -> findCar.getImages().remove(url)));

        return carRepository.save(findCar);
    }

    public Car findCar(long carId) {
        Car findCar = findVerifiedCar(carId);
        return findCar;
    }

    @Transactional(readOnly = true)
    public List<Car> findAllCars() {
        return carRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Car> findAvailableCars() {
        return carRepository.findByAvailable(true);
    }
    
    public void deleteCar(long carId, long employeeId) {
        // employee 호출할 경우 사용하는 코드
//        Map<String, Object> employee = employeeFeignClient.getEmployeeById(employeeId);
//
//        if (!employee.containsKey("employeeId")) {
//            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
//        }
//
//        if (!employee.get("username").equals("관리자")) {
//            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
//        }
        Car findCar = findVerifiedCar(carId);

        carRepository.delete(findCar);
    }

    public Car findVerifiedCar(long carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        return optionalCar.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.CAR_NOT_FOUND));
    }
}
