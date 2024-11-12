package com.tiffy.repository;

import com.tiffy.entity.ChatRoom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
    ChatRoom findChatRoomByUsers(Long user1Id, Long user2Id);
    void createChatRoom(ChatRoom chatRoom);
    List<ChatRoom> findChatRoomsByUserId(Long userId);
}
