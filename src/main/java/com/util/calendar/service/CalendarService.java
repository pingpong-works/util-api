package com.util.calendar.service;

import com.util.calendar.entity.Calendar;
import com.util.calendar.repository.CalendarRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.DepartmentFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final DepartmentFeignClient departmentFeignClient;

    public CalendarService(CalendarRepository calendarRepository,
                           DepartmentFeignClient departmentFeignClient) {
        this.calendarRepository = calendarRepository;
        this.departmentFeignClient = departmentFeignClient;
    }

    public Calendar createCalendar(Calendar calendar, long departmentId) throws IllegalArgumentException {
        // department 호출할 경우 사용하는 코드
//        Map<String, Object> department = departmentFeignClient.getDepartmentById(departmentId);
//
//        if (department.containsKey("departmentId")) {
//            Long fetchDepartmentId = (Long) department.get("departmentId");
//            String departmentName = (String) department.get("name");
//
//            calendar.setDepartmentId(fetchDepartmentId);
//            calendar.setName(departmentName);
//
//            return calendarRepository.save(calendar);
//        }
//        else {
//            throw new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND);
//        }

        // department없이 사용하는 일반 코드, 현재 calendar에는 name이 null로 저장됨
        calendar.setDepartmentId(departmentId);
        return calendarRepository.save(calendar);
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
