package com.util.book.repository;

import com.util.book.entity.CarBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CarBookRepository extends JpaRepository<CarBook, Long> {
    @Query("SELECT COUNT(cb) > 0 FROM CarBook cb WHERE cb.car.carId = :carId AND (:bookStart < cb.bookEnd AND :bookEnd > cb.bookStart)")
    boolean existsOverlappingBooking(@Param("carId") Long carId,
                                     @Param("bookStart") LocalDateTime bookStart,
                                     @Param("bookEnd") LocalDateTime bookEnd);
}
