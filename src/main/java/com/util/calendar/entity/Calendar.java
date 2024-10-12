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
    private Long calendarId;

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

    @OneToOne
    @JoinColumn(name = "car_book_id")
    private CarBook carBook;

    @OneToOne
    @JoinColumn(name = "room_book_id")
    private RoomBook roomBook;
}
