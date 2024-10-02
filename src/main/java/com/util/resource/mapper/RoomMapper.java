package com.util.resource.mapper;

import com.util.resource.dto.RoomDto;
import com.util.resource.entity.Room;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RoomMapper {

    Room roomPostDtoToRoom(RoomDto.Post requestBody);

    Room roomPatchDtoToRoom(RoomDto.Patch requestBody);

    RoomDto.Response roomToRoomResponseDto(Room room);

    List<RoomDto.Response> roomsToRoomResponseDtos(List<Room> rooms);
}
