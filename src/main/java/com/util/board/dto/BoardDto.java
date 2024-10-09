package com.util.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class BoardDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String title;

        @NotBlank
        private String content;

        @NotBlank
        private String category;

        private List<String> imageUrls;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private String title;

        private String content;

        private String category;

        private List<String> imageUrls;

        private List<String> imagesToDelete;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long boardId;
        private long employeeId;
        private String employeeName;
        private String title;
        private String content;
        private String category;
        private int views;
        private int commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private List<String> imageUrls;
        private List<BoardCommentDto.Response> boardCommentList;
    }
}
