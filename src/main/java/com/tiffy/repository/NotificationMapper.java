package com.tiffy.repository;

import com.tiffy.entity.Notification;

import java.util.List;

public interface NotificationMapper {
    List<Notification> getUnreadNotifications(Long userId);
    void createNotification(Notification notification);
    void markAsRead(Long userId);
}
