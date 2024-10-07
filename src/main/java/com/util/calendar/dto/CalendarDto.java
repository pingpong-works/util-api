package com.util.calendar.dto;

import com.util.calendar.entity.Calendar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CalendarDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String title;

        @NotBlank
        private String content;

        @NotNull
        private LocalDateTime startTime;

        @NotNull
        private LocalDateTime endTime;

        @NotNull
        private Calendar.bookType book;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private String title;

        private String content;

        private LocalDateTime startTime;

        private LocalDateTime endTime;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long calendarId;
        private String title;
        private String content;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Calendar.bookType book;
        private long departmentId;
        private String name;
        private long carBookId;
        private long roomBookId;
    }
}
