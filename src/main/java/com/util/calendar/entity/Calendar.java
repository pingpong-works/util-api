package com.util.calendar.entity;

import com.util.book.entity.CarBook;
import com.util.book.entity.RoomBook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long calendarId;

    @Column(name = "calendar_title")
    private String title;

    @Column(name = "calendar_content")
    private String content;

    @Column(name = "calendar_start_time")
    private LocalDateTime startTime;

    @Column(name = "calendar_end_time")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "calendar_book_type")
    private bookType book = bookType.Car;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "department_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "car_book_id")
    private CarBook carBook;

    public void setCarBook(CarBook carBook) {
        this.carBook = carBook;
        if (!carBook.getCalendars().contains(this)) {
            carBook.getCalendars().add(this);
        }
    }

    @ManyToOne
    @JoinColumn(name = "room_book_id")
    private RoomBook roomBook;

    public void setRoomBook(RoomBook roomBook) {
        this.roomBook = roomBook;
        if (!roomBook.getCalendars().contains(this)) {
            roomBook.getCalendars().add(this);
        }
    }

    public enum bookType {
        Car("차 예약"),
        Room("회의실 예약");


        @Getter
        private final String book;
        bookType(String book) {
            this.book = book;
        }
    }
}
