package com.tiffy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChatRoom {
    private Long id;
    private Long user1Id;
    private Long user2Id;

    public ChatRoom(Long user1Id, Long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }
}
