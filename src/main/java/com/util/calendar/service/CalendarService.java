package com.util.calendar.service;

import com.alarm.kafka.UtilProducer;
import com.util.book.entity.RoomBook;
import com.util.calendar.entity.Calendar;
import com.util.calendar.repository.CalendarRepository;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.DepartmentFeignClient;
import com.util.feign.EmployeeFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final DepartmentFeignClient departmentFeignClient;
    private final UtilProducer utilProducer;
    private final EmployeeFeignClient employeeFeignClient;

    public CalendarService(CalendarRepository calendarRepository,
                           DepartmentFeignClient departmentFeignClient, UtilProducer utilProducer, EmployeeFeignClient employeeFeignClient) {
        this.calendarRepository = calendarRepository;
        this.departmentFeignClient = departmentFeignClient;
        this.utilProducer = utilProducer;
        this.employeeFeignClient = employeeFeignClient;
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

        Calendar savedCalendar = calendarRepository.save(calendar);

        //일정 등록시 알림 송부 기능
        sendCalendarAlarm(savedCalendar, "등록되었습니다.");

        return savedCalendar;
    }

    public Calendar updateCalendar(Calendar calendar, long calendarId, long departmentId) {
        Calendar findCalendar = findVerifiedCalendar(calendarId);

        if (findCalendar.getDepartmentId() != departmentId) {
            throw new BusinessLogicException(ExceptionCode.CALENDAR_UNAUTHORIZED_ACTION);
        }

        boolean isChangeTime = false;

        Optional.ofNullable(calendar.getTitle())
                .ifPresent(title -> findCalendar.setTitle(title));
        Optional.ofNullable(calendar.getContent())
                .ifPresent(content -> findCalendar.setContent(content));

        if (calendar.getStartTime()!= null && !calendar.getStartTime().equals(findCalendar.getStartTime())) {
            findCalendar.setStartTime(calendar.getStartTime());
            isChangeTime = true;
        }

        Optional.ofNullable(calendar.getEndTime())
                .ifPresent(endTime -> findCalendar.setEndTime(endTime));

        Calendar savedCalendar = calendarRepository.save(findCalendar);

        //일정이 변경되었을 경우 알림 전송
        if(isChangeTime) {
            sendCalendarAlarm(savedCalendar, "변경되었습니다.");
        }

        return savedCalendar;
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


    //일정 알림
    private void sendCalendarAlarm(Calendar calendar, String status) {
        String startTime = changeFormatTime(calendar.getStartTime());

        List<Long> employeeIds = employeeFeignClient.getEmployeeIdsByDepartment(calendar.getDepartmentId());
        employeeIds.stream().forEach( id-> {
            utilProducer.sendCalendarNotification( id,
                    String.format("[%s]에 일정[%s]이 %s", startTime, calendar.getTitle(), status),
                    calendar.getCalendarId());
        });
    }

    //날짜형식 변경
    private String changeFormatTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return localDateTime.format(formatter);
    }
}
