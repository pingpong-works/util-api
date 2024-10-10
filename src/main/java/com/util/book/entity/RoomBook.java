package com.util.book.entity;

import com.util.calendar.entity.Calendar;
import com.util.resource.entity.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class RoomBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomBookId;

    @Column(name = "room_book_start")
    private LocalDateTime bookStart;

    @Column(name = "room_book_end")
    private LocalDateTime bookEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_book_purpose_type")
    private PurposeType purpose = PurposeType.MEETING;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_book_status")
    private StatusType status = StatusType.PENDING;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public void setRoom(Room room) {
        this.room = room;
        if (!room.getRoomBooks().contains(this)) {
            room.getRoomBooks().add(this);
        }
    }

    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        if (!calendar.getRoomBooks().contains(this)) {
            calendar.setRoomBook(this);
        }
    }

    public enum PurposeType {
        MEETING("회의"),
        WORKSHOP("워크샵"),
        TRAINING("교육"),
        OTHER("기타");

        @Getter
        private final String purpose;

        PurposeType(String purpose) {
            this.purpose = purpose;
        }
    }

    public enum StatusType {
        PENDING("신청"),
        CONFIRMED("확정"),
        CANCELLED("취소");

        @Getter
        private final String status;

        StatusType(String status) {
            this.status = status;
        }
    }
}
