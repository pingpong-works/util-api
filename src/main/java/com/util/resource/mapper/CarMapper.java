package com.util.resource.mapper;

import com.util.resource.dto.CarDto;
import com.util.resource.entity.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    Car carPostDtoToCar(CarDto.Post requestBody);

    Car carPatchDtoToCar(CarDto.Patch requestBody);

    CarDto.Response carToCarResponseDto(Car car);

    List<CarDto.Response> carsToCarResponseDtos(List<Car> cars);
}
