package com.alarm.kafka;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationMessage {
    private Long employeeId;
    private String message;
    private Long typeId;
    private NotificationType type;

    public enum NotificationType {
        NOTICE,
        BOOK_CAR,
        BOOK_ROOM,
        CALENDAR;
    }
}
