package com.util.resource.dto;

import com.util.resource.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class CarDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String name;

        @NotBlank
        private String number;

        @NotNull
        private Car.FuelType fuel;

        private Map<String, String> images;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private String name;

        private String number;

        private Car.FuelType fuel;

        private Map<String, String> images;

        private List<String> imagesToDelete;

        private Boolean available;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long carId;
        private String name;
        private String number;
        private Car.FuelType fuel;
        private boolean available;
        private Map<String, String> images;
    }
}
