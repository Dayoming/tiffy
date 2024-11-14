package com.tiffy.controller;

import com.tiffy.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread/{chatRoomId}/{userId}")
    public Integer getUnreadNotificationsCount(@PathVariable Long chatRoomId, @PathVariable Long userId) {
        System.out.println("No reading Message: " + notificationService.getUnreadNotificationsCount(chatRoomId, userId));
        return notificationService.getUnreadNotificationsCount(chatRoomId, userId);
    }

    @PostMapping("/markAsRead/{chatRoomId}/{userId}")
    public void markAsRead(@PathVariable Long chatRoomId, @PathVariable Long userId) {
        notificationService.markAsRead(chatRoomId, userId);
    }
}
