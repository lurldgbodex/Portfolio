package tech.sgcor.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.sgcor.notification.dto.NotificationDto;
import tech.sgcor.notification.dto.NotificationResponse;
import tech.sgcor.notification.model.Notification;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final String QUEUE_NAME = "notification_queue";
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Notification> redisTemplate;

    public NotificationResponse sendNotification(NotificationDto notificationDto) {
        // store notification in redis for temp storage
        Notification notification = new Notification();
        notification.setUserId(notificationDto.getUser_id());
        notification.setMessage(notificationDto.getMessage());

        redisTemplate.opsForValue().set(notification.getId(), notification);

        // send notification message to rabbitmq queue for processing
        rabbitTemplate.convertAndSend(QUEUE_NAME, notification);

        return new NotificationResponse("Notification sent successfully");
    }
}
