package com.util.calendar.mapper;

import com.util.calendar.dto.CalendarDto;
import com.util.calendar.entity.Calendar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalendarMapper {

    Calendar calendarPostDtoToCalendar(CalendarDto.Post requestBody);

    Calendar calendarPatchDtoToCalendar(CalendarDto.Patch requestBody);

    @Mapping(source = "carBook.carBookId", target = "carBookId")
    @Mapping(source = "roomBook.roomBookId", target = "roomBookId")
    CalendarDto.Response calendarToCalendarResponseDto(Calendar calendar);

    List<CalendarDto.Response> calendarToCalendarResponseDtos(List<Calendar> calendars);
}
