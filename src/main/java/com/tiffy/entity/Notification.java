package com.tiffy.entity;

import lombok.Data;

@Data
public class Notification {
    private Long id;
    private Long chatRoomId;
    private Long userId;
    private Boolean isRead;

    public Notification(Long chatRoomId, Long userId) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
    }
}
