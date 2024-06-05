package tech.sgcor.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
public class Notification {
    @Id
    private String id;
    private String userId;
    private String message;
}
