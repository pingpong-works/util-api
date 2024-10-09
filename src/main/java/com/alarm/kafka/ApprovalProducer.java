package com.alarm.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApprovalProducer {
    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public void sendNoticeNotification (Long employeeId, String message, Long boardId ) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .employeeId(employeeId)
                .typeId(boardId)
                .type(NotificationMessage.NotificationType.NOTICE)
                .build();
        kafkaTemplate.send("notice-topic",notificationMessage);
    }

    public void sendBookCarNotification (Long employeeId, String message, Long carBookId ) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .employeeId(employeeId)
                .typeId(carBookId)
                .type(NotificationMessage.NotificationType.BOOK_CAR)
                .build();
        kafkaTemplate.send("book-car-topic",notificationMessage);
    }

    public void sendBookRoomNotification (Long employeeId, String message, Long roomBookId ) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .employeeId(employeeId)
                .typeId(roomBookId)
                .type(NotificationMessage.NotificationType.BOOK_ROOM)
                .build();
        kafkaTemplate.send("book-room-topic",notificationMessage);
    }

    public void sendCalendarNotification (Long employeeId, String message, Long calendarId ) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .employeeId(employeeId)
                .typeId(calendarId)
                .type(NotificationMessage.NotificationType.CALENDAR)
                .build();
        kafkaTemplate.send("calendar-topic",notificationMessage);
    }

}
