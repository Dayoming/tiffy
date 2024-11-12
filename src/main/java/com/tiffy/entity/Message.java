package com.tiffy.entity;

import lombok.Data;

@Data
public class Message {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String timestamp;
}
