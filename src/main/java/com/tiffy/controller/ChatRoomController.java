package com.tiffy.controller;

import com.tiffy.entity.ChatRoom;
import com.tiffy.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/getOrCreate")
    public ChatRoom getOrCreateChatRoom(@AuthenticationPrincipal String user1Name, @RequestParam Long user2Id) {
        return chatRoomService.getOrCreateChatRoom(user1Name, user2Id);
    }

    @GetMapping("/findChatRoomsByUserId")
    public List<ChatRoom> findChatRoomsByUserId(@AuthenticationPrincipal String userId) {
        return chatRoomService.findChatRoomsByUserId(userId);
    }
}

