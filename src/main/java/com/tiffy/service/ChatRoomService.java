package com.tiffy.service;

import com.tiffy.entity.ChatRoom;
import com.tiffy.entity.User;
import com.tiffy.repository.ChatRoomMapper;
import com.tiffy.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {
    private final ChatRoomMapper chatRoomMapper;
    private final UserMapper userMapper;

    @Autowired
    public ChatRoomService(ChatRoomMapper chatRoomMapper, UserMapper userMapper) {
        this.chatRoomMapper = chatRoomMapper;
        this.userMapper = userMapper;
    }

    // 채팅방 생성 혹은 불러오기
    public ChatRoom getOrCreateChatRoom(String user1Name, Long user2Id) {
        User user1 = userMapper.findUserByUsername(user1Name);
        ChatRoom chatRoom = chatRoomMapper.findChatRoomByUsers(user1.getId(), user2Id);
        if (chatRoom == null) { // 만약 두 유저가 포함된 채팅방이 없다면 새 채팅방 만들기
            chatRoom = new ChatRoom(user1.getId(), user2Id);
            chatRoomMapper.createChatRoom(chatRoom);
        }
        return chatRoom;
    }

    // 해당 유저가 포함된 채팅방을 모두 가져오기
    public List<ChatRoom> findChatRoomsByUserId(String userId) {
        Long id = userMapper.findUserByUsername(userId).getId();
        return chatRoomMapper.findChatRoomsByUserId(id);
    }
}
