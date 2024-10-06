package com.util.calendar.mapper;

import com.util.book.dto.CarBookDto;
import com.util.calendar.dto.CalendarDto;
import com.util.calendar.entity.Calendar;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalendarMapper {

    Calendar calendarPostDtoToCalendar(CalendarDto.Post requestBody);

    Calendar calendarPatchDtoToCalendar(CalendarDto.Patch requestBody);

    CalendarDto.Response calendarToCalendarResponseDto(Calendar calendar);

    List<CarBookDto.Response> calendarToCalendarResponseDtos(List<Calendar> calendars);
}
