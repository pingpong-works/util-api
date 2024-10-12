package com.util.calendar.service;

import com.util.calendar.entity.Calendar;
import com.util.calendar.repository.CalendarRepository;
import com.util.dto.SingleResponseDto;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.dto.DepartmentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final AuthFeignClient authFeignClient;

    public CalendarService(CalendarRepository calendarRepository,
                           AuthFeignClient authFeignClient) {
        this.calendarRepository = calendarRepository;
        this.authFeignClient = authFeignClient;
    }

    public Calendar createCalendar(Calendar calendar, long departmentId) throws IllegalArgumentException {
        SingleResponseDto<DepartmentDto> departmentDto = authFeignClient.getDepartmentById(departmentId);

        if (departmentDto != null && departmentDto.getData().getId() != null) {
            Long fetchDepartmentId = departmentDto.getData().getId(); // Long 타입이므로 바로 사용 가능
            String departmentName = departmentDto.getData().getName(); // 부서 이름 가져오기

            calendar.setDepartmentId(fetchDepartmentId);
            calendar.setName(departmentName);

            return calendarRepository.save(calendar);
        }
        else {
            throw new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND);
        }
    }

    public Calendar updateCalendar(Calendar calendar, long calendarId, long departmentId) {
        Calendar findCalendar = findVerifiedCalendar(calendarId);

        if (findCalendar.getDepartmentId() != departmentId) {
            throw new BusinessLogicException(ExceptionCode.CALENDAR_UNAUTHORIZED_ACTION);
        }

        Optional.ofNullable(calendar.getTitle())
                .ifPresent(title -> findCalendar.setTitle(title));
        Optional.ofNullable(calendar.getContent())
                .ifPresent(content -> findCalendar.setContent(content));
        Optional.ofNullable(calendar.getStartTime())
                .ifPresent(startTime -> findCalendar.setStartTime(startTime));
        Optional.ofNullable(calendar.getEndTime())
                .ifPresent(endTime -> findCalendar.setEndTime(endTime));

        return calendarRepository.save(findCalendar);
    }

    public Calendar findCalendar(long calendarId) {
        return findVerifiedCalendar(calendarId);
    }

    @Transactional(readOnly = true)
    public List<Calendar> findAllCalendar() {
        return calendarRepository.findAll();
    }

    public void deleteCalendar(long calendarId, long departmentId) {
        Calendar findCalendar = findVerifiedCalendar(calendarId);

        if (findCalendar.getDepartmentId() == departmentId) {
            calendarRepository.delete(findCalendar);
        } else {
            throw new BusinessLogicException(ExceptionCode.CALENDAR_UNAUTHORIZED_ACTION);
        }
    }

    public Calendar findVerifiedCalendar(long calendarId) {
        Optional<Calendar> optionalCalendar = calendarRepository.findById(calendarId);
        return optionalCalendar.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.CALENDAR_NOT_FOUND));
    }
}
