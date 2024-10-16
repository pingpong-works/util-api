package com.util.feign;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeData {
    private Long employeeId;
    private String name;
    private String departmentName;
    private Long departmentId;
    private String email;
}