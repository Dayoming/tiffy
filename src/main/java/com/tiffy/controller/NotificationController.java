package com.tiffy.controller;

import com.tiffy.entity.Notification;
import com.tiffy.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread/{userId}")
    public List<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    @PostMapping("/markAsRead/{userId}")
    public void markAsRead(@PathVariable Long userId) {
        notificationService.markAsRead(userId);
    }
}
