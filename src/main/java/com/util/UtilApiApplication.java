package com.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.util", "com.alarm"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class UtilApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(UtilApiApplication.class, args);
  }
}
