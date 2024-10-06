package com.util.book.entity;

import com.util.resource.entity.Car;
import com.util.calendar.entity.Calendar;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CarBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long carBookId;

    @Column(name = "car_book_start")
    private LocalDateTime bookStart;

    @Column(name = "car_book_end")
    private LocalDateTime bookEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_book_purpose_type")
    private PurposeType purpose = PurposeType.BUSINESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_book_status")
    private StatusType status = StatusType.PENDING;


    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    public void setCar(Car car) {
        this.car = car;
        if (!car.getCarBooks().contains(this)) {
            car.getCarBooks().add(this);
        }
    }

    @OneToMany(mappedBy = "carBook", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Calendar> calendars = new ArrayList<>();

    public void setCalendar(Calendar calendar) {
        calendars.add(calendar);
        if (calendar.getCarBook() != this) {
            calendar.setCarBook(this);
        }
    }

    public enum PurposeType {
        BUSINESS("업무"),
        PERSONAL("개인"),
        MAINTENANCE("정비"),
        OTHER("기타");

        @Getter
        private final String purpose;
        PurposeType(String purpose) {
            this.purpose = purpose;
        }
    }

    public enum StatusType {
        PENDING("검토"),
        CONFIRMED("확인"),
        CANCELLED("취소");

        @Getter
        private final String status;
        StatusType(String status) {
            this.status = status;
        }
    }
}
