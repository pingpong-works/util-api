package com.util.resource.repository;

import com.util.resource.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT c FROM Car c WHERE c.available = :available")
    List<Room> findByAvailable(boolean available);
}
