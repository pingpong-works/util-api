package com.util.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "auth-api")
public interface EmployeeFeignClient {
    @GetMapping("/employees/{id}")
    Map<String, Object> getEmployeeById(@PathVariable("id") Long employeeId);

    @GetMapping("/employees")
    List<Long> getEmployeeIds();
}
