package com.tiffy.service;

import com.tiffy.entity.Notification;
import com.tiffy.repository.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationMapper.getUnreadNotifications(userId);
    }

    public void createNotification(Long chatRoomId, Long userId) {
        notificationMapper.createNotification(new Notification(chatRoomId, userId));
    }

    public void markAsRead(Long userId) {
        notificationMapper.markAsRead(userId);
    }
}
