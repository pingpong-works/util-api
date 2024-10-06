package com.util.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

public class RoomDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String name;

        @Positive
        private int maxCapacity;

        private List<String> equipment;

        @NotBlank
        private String location;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private String name;

        private int maxCapacity;

        private List<String> equipment;

        private List<String> equipmentsToDelete;

        private String location;

        private Boolean available;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long roomId;
        private String name;
        private int maxCapacity;
        private List<String> equipment;
        private String location;
        private boolean available;
    }
}
