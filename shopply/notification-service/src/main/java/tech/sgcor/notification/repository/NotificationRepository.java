package tech.sgcor.notification.repository;

import org.springframework.data.repository.CrudRepository;
import tech.sgcor.notification.model.Notification;

public interface NotificationRepository extends CrudRepository<Notification, String> {
}
