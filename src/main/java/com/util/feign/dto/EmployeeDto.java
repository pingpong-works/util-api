package com.util.feign.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {
    private Long employeeId;
    private String name;
    private Long departmentId;
}