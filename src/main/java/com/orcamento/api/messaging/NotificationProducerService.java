package com.orcamento.api.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.orcamento.api.dto.NotificationEventDTO;

@Service
public class NotificationProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.notification.queue:notifications}")
    private String notificationQueue;

    public NotificationProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(NotificationEventDTO event) {
        rabbitTemplate.convertAndSend(notificationQueue, event);
    }
}
