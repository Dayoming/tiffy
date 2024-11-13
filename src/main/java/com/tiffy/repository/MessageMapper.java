package com.tiffy.repository;

import com.tiffy.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> getMessagesByChatRoomId(Long chatRoomId);
    Message getLastMessageByChatRoomId(Long chatRoomId);
    void saveMessage(Message message);
}
