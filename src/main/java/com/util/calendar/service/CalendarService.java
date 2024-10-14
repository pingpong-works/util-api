package com.util.calendar.service;

import com.alarm.kafka.UtilProducer;
import com.util.calendar.entity.Calendar;
import com.util.calendar.repository.CalendarRepository;
import com.util.dto.SingleResponseDto;
import com.util.exception.BusinessLogicException;
import com.util.exception.ExceptionCode;
import com.util.feign.AuthFeignClient;
import com.util.feign.dto.DepartmentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Transactional
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final AuthFeignClient authFeignClient;
    private final UtilProducer utilProducer;

    public CalendarService(CalendarRepository calendarRepository,
                           AuthFeignClient authFeignClient, UtilProducer utilProducer) {
        this.calendarRepository = calendarRepository;
        this.authFeignClient = authFeignClient;
        this.utilProducer = utilProducer;
    }

    public Calendar createCalendar(Calendar calendar, long departmentId) throws IllegalArgumentException {
        SingleResponseDto<DepartmentDto> departmentDto = authFeignClient.getDepartmentById(departmentId);

        if (departmentDto != null && departmentDto.getData().getId() != null) {
            Long fetchDepartmentId = departmentDto.getData().getId(); // Long 타입이므로 바로 사용 가능
            String departmentName = departmentDto.getData().getName(); // 부서 이름 가져오기

            calendar.setDepartmentId(fetchDepartmentId);
            calendar.setName(departmentName);

            Calendar savedCalendar = calendarRepository.save(calendar);

            //일정 등록시 알림 송부 기능
            sendCalendarAlarm(savedCalendar, "등록되었습니다.");

            return savedCalendar;
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

        AtomicBoolean isChangeTime = new AtomicBoolean(false);

        Optional.ofNullable(calendar.getTitle())
                .ifPresent(title -> findCalendar.setTitle(title));
        Optional.ofNullable(calendar.getContent())
                .ifPresent(content -> findCalendar.setContent(content));
        Optional.ofNullable(calendar.getStartTime())
                .ifPresent(startTime -> {
                    if (!startTime.equals(findCalendar.getStartTime())) {
                        findCalendar.setStartTime(startTime);
                        isChangeTime.set(true);  // 시간 변경 시 true로 설정
                    }
                });

        Optional.ofNullable(calendar.getEndTime())
                .ifPresent(endTime -> {
                    if (!endTime.equals(findCalendar.getEndTime())) {
                        findCalendar.setEndTime(endTime);
                        isChangeTime.set(true);  // 시간 변경 시 true로 설정
                    }
                });


        Calendar savedCalendar = calendarRepository.save(findCalendar);

        //일정이 변경되었을 경우 알림 전송
        if(isChangeTime.get()) {
            sendCalendarAlarm(savedCalendar, "변경되었습니다.");
        }

        return savedCalendar;
    }

    public Calendar findCalendar(long calendarId) {
        return findVerifiedCalendar(calendarId);
    }

    @Transactional(readOnly = true)
    public List<Calendar> findCalendarsByDepartment(long departmentId) {
        return calendarRepository.findByDepartmentId(departmentId);
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
        String endTime = changeFormatTime(calendar.getEndTime());

        List<Long> employeeIds = authFeignClient.getEmployeeIdsByDepartment(calendar.getDepartmentId());
        employeeIds.stream().forEach( id-> {
            utilProducer.sendCalendarNotification( id,
                    String.format("[%s] 일정이 [%s] ~ [%s]로 %s", calendar.getTitle(), startTime, endTime, calendar.getTitle(), status),
                    calendar.getCalendarId());
        });
    }

    //날짜형식 변경
    private String changeFormatTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return localDateTime.format(formatter);
    }
}
