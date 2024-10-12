package com.util.book.entity;

import com.util.resource.entity.Car;
import com.util.calendar.entity.Calendar;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CarBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carBookId;

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

    @OneToOne(mappedBy = "carBook", cascade = CascadeType.ALL)
    private Calendar calendar;

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
