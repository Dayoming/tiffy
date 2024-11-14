package com.tiffy.controller;

import com.tiffy.entity.Message;
import com.tiffy.service.MessageService;
import com.tiffy.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(MessageService messageService, NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/user/messages/{receiverId}")
    public void sendMessage(@Payload Message message) {
        messageService.saveMessage(message);
        notificationService.createNotification(message.getChatRoomId(), message.getReceiverId());

        // 메시지를 수신자에게만 전송
        messagingTemplate.convertAndSend("/queue/user/" + message.getReceiverId(), message);
    }
}

