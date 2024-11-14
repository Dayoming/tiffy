package com.tiffy.service;

import com.tiffy.entity.Notification;
import com.tiffy.repository.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public Integer getUnreadNotificationsCount(Long chatRoomId, Long userId) {
        return notificationMapper.getUnreadNotificationsCount(chatRoomId, userId);
    }

    public void createNotification(Long chatRoomId, Long userId) {
        notificationMapper.createNotification(new Notification(chatRoomId, userId));
    }

    public void markAsRead(Long chatRoomId, Long userId) {
        notificationMapper.markAsRead(new Notification(chatRoomId, userId));
    }
}
