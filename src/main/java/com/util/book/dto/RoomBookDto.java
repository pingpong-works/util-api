package com.util.book.dto;

import com.util.book.entity.RoomBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private Long roomId;

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
        private Long roomBookId;
        private LocalDateTime bookStart;
        private LocalDateTime bookEnd;
        private RoomBook.PurposeType purpose;
        private RoomBook.StatusType status;
        private Long employeeId;
        private String employeeName;
        private Long roomId;
    }
}
