package com.util.book.repository;

import com.util.book.entity.RoomBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RoomBookRepository extends JpaRepository<RoomBook, Long> {
    @Query("SELECT COUNT(rb) > 0 FROM RoomBook rb WHERE rb.room.roomId = :roomId AND (:bookStart < rb.bookEnd AND :bookEnd > rb.bookStart)")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("bookStart") LocalDateTime bookStart,
                                     @Param("bookEnd") LocalDateTime bookEnd);
}
