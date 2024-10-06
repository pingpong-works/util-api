package com.util.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "department-service")
public interface DepartmentFeignClient {
    @GetMapping("/department/{id}")
    Map<String, Object> getDepartmentById(@PathVariable("id") Long departmentId);
}
