package com.util.book.mapper;

import com.util.book.dto.RoomBookDto;
import com.util.book.entity.RoomBook;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomBookMapper {

    RoomBook roomBookPostDtoToRoomBook(RoomBookDto.Post requestBody);

    RoomBook roomBookPatchDtoToRoomBook(RoomBookDto.Patch requestBody);

    RoomBookDto.Response roomBookToRoomBookResponseDto(RoomBook roomBook);

    List<RoomBookDto.Response> roomBooksToRoomBookResponseDtos(List<RoomBook> roomBooks);
}
