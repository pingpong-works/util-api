package com.util.book.dto;

import com.util.book.entity.RoomBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class RoomBookDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotNull
        private LocalDateTime bookStart;

        @NotNull
        private LocalDateTime bookEnd;

        @NotNull
        private RoomBook.PurposeType purpose;

        @NotNull
        private RoomBook.StatusType status;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private LocalDateTime bookStart;

        private LocalDateTime bookEnd;

        private RoomBook.PurposeType purpose;

        private RoomBook.StatusType status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long carBookId;
        private LocalDateTime bookStart;
        private LocalDateTime bookEnd;
        private RoomBook.PurposeType purpose;
        private RoomBook.StatusType status;
        private long employeeId;
        private String employeeName;
        private long carId;
    }
}
