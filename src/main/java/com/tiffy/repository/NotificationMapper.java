package com.tiffy.repository;

import com.tiffy.entity.Notification;

public interface NotificationMapper {
    Integer getUnreadNotificationsCount(Long chatRoomId, Long userId);
    void createNotification(Notification notification);
    void markAsRead(Notification notification);
}
