package tech.sgcor.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tech.sgcor.notification.dto.NotificationDto;

@Component
@Slf4j
public class NotificationEventReceiver {

    @RabbitListener(queues = "notification_queue")
    public void receiveNotification(NotificationDto notificationDto) {
      log.info("Received notification: " + notificationDto.getMessage());

      // todo: implement logic to send email
    }
}
