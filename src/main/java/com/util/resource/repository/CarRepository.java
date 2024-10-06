package com.util.resource.repository;

import com.util.resource.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.available = :available")
    List<Car> findByAvailable(boolean available);
}
