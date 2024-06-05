package tech.sgcor.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.notification.dto.NotificationDto;
import tech.sgcor.notification.dto.NotificationResponse;
import tech.sgcor.notification.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResponse sendNotification(@RequestBody @Valid NotificationDto notificationDto) {
        return notificationService.sendNotification(notificationDto);
    }

}
