package com.util.calendar.entity;

import com.util.book.entity.CarBook;
import com.util.book.entity.RoomBook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDateTime endTime;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "department_name")
    private String name;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CarBook> carBooks = new ArrayList<>();

    public void setCarBook(CarBook carBook) {
        carBooks.add(carBook);
        if (carBook.getCalendar() != this) {
            carBook.setCalendar(this);
        }
    }

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RoomBook> roomBooks = new ArrayList<>();

    public void setRoomBook(RoomBook roomBook) {
        roomBooks.add(roomBook);
        if (roomBook.getCalendar() != this) {
            roomBook.setCalendar(this);
        }
    }
}
