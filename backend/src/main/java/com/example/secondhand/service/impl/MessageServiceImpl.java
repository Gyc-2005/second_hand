
package com.example.secondhand.service.impl;

import com.example.secondhand.dto.MessageCreateDTO;
import com.example.secondhand.dto.MessageResponseDTO;
import com.example.secondhand.entity.Message;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.repository.MessageRepository;
import com.example.secondhand.repository.ProductRepository;
import com.example.secondhand.repository.UserRepository;
import com.example.secondhand.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Message create(Integer senderId, MessageCreateDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (senderId.equals(dto.getReceiverId())) {
            throw new RuntimeException("不能给自己发送留言");
        }

        Message message = new Message();
        message.setProductId(dto.getProductId());
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());

        return messageRepository.save(message);
    }

    @Override
    public List<Message> listByProduct(Integer productId) {
        return messageRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<Message> listByReceiver(Integer receiverId) {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    @Override
    public List<MessageResponseDTO> listByReceiverWithNickname(Integer receiverId) {
        List<Message> messages = messageRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        
        return messages.stream().map(message -> {
            User sender = userRepository.findById(message.getSenderId()).orElse(null);
            User receiver = userRepository.findById(message.getReceiverId()).orElse(null);
            
            String senderNickname = sender != null ? 
                (sender.getNickname() != null && !sender.getNickname().isEmpty() ? sender.getNickname() : sender.getUsername()) : "未知用户";
            String receiverNickname = receiver != null ? 
                (receiver.getNickname() != null && !receiver.getNickname().isEmpty() ? receiver.getNickname() : receiver.getUsername()) : "未知用户";
            
            return new MessageResponseDTO(
                message.getId(),
                message.getProductId(),
                message.getSenderId(),
                senderNickname,
                message.getReceiverId(),
                receiverNickname,
                message.getContent(),
                message.getIsRead(),
                message.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Integer countUnread(Integer receiverId) {
        return messageRepository.countUnreadByReceiverId(receiverId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer receiverId) {
        messageRepository.markAllAsRead(receiverId);
    }

    @Override
    @Transactional
    public Message reply(Integer senderId, Integer originalMessageId, String content) {
        Message originalMessage = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        Message replyMessage = new Message();
        replyMessage.setProductId(originalMessage.getProductId());
        replyMessage.setSenderId(senderId);
        replyMessage.setReceiverId(originalMessage.getSenderId());
        replyMessage.setContent(content);

        return messageRepository.save(replyMessage);
    }
}
