
package com.example.secondhand.service;

import com.example.secondhand.dto.MessageCreateDTO;
import com.example.secondhand.dto.MessageResponseDTO;
import com.example.secondhand.entity.Message;

import java.util.List;

public interface MessageService {

    Message create(Integer senderId, MessageCreateDTO dto);

    List<Message> listByProduct(Integer productId);

    List<Message> listByReceiver(Integer receiverId);

    List<MessageResponseDTO> listByReceiverWithNickname(Integer receiverId);

    Integer countUnread(Integer receiverId);

    void markAllAsRead(Integer receiverId);

    Message reply(Integer senderId, Integer originalMessageId, String content);
}
