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
                .type(NotificationMessage.NotificationType.CAR_BOOK)
                .build();
        kafkaTemplate.send("notice-topic",notificationMessage);
    }

}
