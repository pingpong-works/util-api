package com.util.book.mapper;

import com.util.book.dto.CarBookDto;
import com.util.book.entity.CarBook;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarBookMapper {

    CarBook carBookPostDtoToCarBook(CarBookDto.Post requestBody);

    CarBook carBookPatchDtoToCarBook(CarBookDto.Patch requestBody);

    CarBookDto.Response carBookToCarBookResponseDto(CarBook carBook);

    List<CarBookDto.Response> carBooksToCarBookResponseDtos(List<CarBook> carBooks);
}
