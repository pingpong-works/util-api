package com.util.feign;

import com.util.dto.SingleResponseDto;
import com.util.feign.dto.DepartmentDto;
import com.util.feign.dto.EmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "auth-api")
public interface AuthFeignClient {

    @GetMapping("/user/employees/{id}")
    SingleResponseDto<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId);

    @GetMapping("/employees")
    List<Long> getEmployeeIds();

    @GetMapping("departments/{department-id}/employees")
    List<Long> getEmployeeIdsByDepartment(@PathVariable("department-id") Long departmentId);

    @GetMapping("/departments/{id}")
    SingleResponseDto<DepartmentDto> getDepartmentById(@PathVariable("id") Long departmentId );
}
