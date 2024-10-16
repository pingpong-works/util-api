package com.util.book.dto;


import com.util.book.entity.CarBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CarBookDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotNull
        private LocalDateTime bookStart;

        @NotNull
        private LocalDateTime bookEnd;

        @NotNull
        private CarBook.PurposeType purpose;

    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private Long carId;

        private LocalDateTime bookStart;

        private LocalDateTime bookEnd;

        private CarBook.PurposeType purpose;

        private CarBook.StatusType status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long carBookId;
        private LocalDateTime bookStart;
        private LocalDateTime bookEnd;
        private CarBook.PurposeType purpose;
        private CarBook.StatusType status;
        private Long employeeId;
        private String employeeName;
        private Long carId;
    }
}
