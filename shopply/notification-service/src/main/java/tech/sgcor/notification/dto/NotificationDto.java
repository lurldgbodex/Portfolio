package tech.sgcor.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationDto {
    private String id;
    @NotBlank(message = "user_id is required")
    private String user_id;
    @NotBlank(message = "message is required")
    private String message;
}
