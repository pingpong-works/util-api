package com.util.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class BoardCommentDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String content;

        private Long parentCommentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {
        @NotBlank
        private String content;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long boardCommentId;
        private Long boardId;
        private Long employeeId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private Long parentCommentId;
        private String content;
        private String employeeName;
    }
}
