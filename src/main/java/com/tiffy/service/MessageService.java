package com.tiffy.service;

import com.tiffy.entity.Message;
import com.tiffy.repository.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class MessageService {
    private final MessageMapper messageMapper;

    @Autowired
    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public List<Message> getMessagesByChatRoomId(Long chatRoomId) {
        return messageMapper.getMessagesByChatRoomId(chatRoomId);
    }

    public void saveMessage(Message message) {
        messageMapper.saveMessage(message);
    }

    public Message getLastMessageByChatRoomId(@PathVariable Long chatRoomId) {
        return messageMapper.getLastMessageByChatRoomId(chatRoomId);
    }

}
