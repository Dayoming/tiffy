package com.tiffy.controller;

import com.tiffy.entity.Message;
import com.tiffy.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{chatRoomId}")
    public List<Message> getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        return messageService.getMessagesByChatRoomId(chatRoomId);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
    }

    @GetMapping("/getLastMessage")
    public Message getLastMessageByChatRoomId(Long chatRoomId) {
        return messageService.getLastMessageByChatRoomId(chatRoomId);
    }
}
