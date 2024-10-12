package com.util.resource.service;

import com.util.dto.SingleResponseDto;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.dto.EmployeeDto;
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
    private final AuthFeignClient authFeignClient;

    public CarService(CarRepository carRepository,
                      AuthFeignClient authFeignClient) {
        this.carRepository = carRepository;
        this.authFeignClient = authFeignClient;
    }

    public Car createCar(Car car, long employeeId) throws IllegalArgumentException {
        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto.getData().getEmployeeId() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        if (!employeeDto.getData().getName().equals("관리자")) {
            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
        }

        return carRepository.save(car);
    }

    public Car updateCar(Car car, long carId, long employeeId, List<String> imagesToDelete) {
        Car findCar = findVerifiedCar(carId);

        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto.getData().getEmployeeId() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        if (!employeeDto.getData().getName().equals("관리자")) {
            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
        }

        Optional.ofNullable(car.getName())
                .ifPresent(name -> findCar.setName(name));
        Optional.ofNullable(car.getNumber())
                .ifPresent(number -> findCar.setNumber(number));
        Optional.ofNullable(car.isAvailable())
                .ifPresent(available -> findCar.setAvailable(available));
        Optional.ofNullable(car.getFuel())
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
        SingleResponseDto<EmployeeDto> employeeDto = authFeignClient.getEmployeeById(employeeId);

        if (employeeDto.getData().getEmployeeId() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        if (!employeeDto.getData().getName().equals("관리자")) {
            throw new BusinessLogicException(ExceptionCode.CAR_UNAUTHORIZED_ACTION);
        }
        Car findCar = findVerifiedCar(carId);

        carRepository.delete(findCar);
    }

    public Car findVerifiedCar(long carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        return optionalCar.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.CAR_NOT_FOUND));
    }
}
