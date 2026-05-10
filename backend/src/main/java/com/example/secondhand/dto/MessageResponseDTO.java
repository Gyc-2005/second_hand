package com.example.secondhand.dto;

import java.time.LocalDateTime;

public class MessageResponseDTO {

    private Integer id;
    private Integer productId;
    private Integer senderId;
    private String senderNickname;
    private Integer receiverId;
    private String receiverNickname;
    private String content;
    private Boolean read;
    private LocalDateTime createdAt;

    public MessageResponseDTO() {}

    public MessageResponseDTO(Integer id, Integer productId, Integer senderId, String senderNickname,
                              Integer receiverId, String receiverNickname, String content, 
                              Boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.receiverId = receiverId;
        this.receiverNickname = receiverNickname;
        this.content = content;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    public String getSenderNickname() { return senderNickname; }
    public void setSenderNickname(String senderNickname) { this.senderNickname = senderNickname; }
    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
    public String getReceiverNickname() { return receiverNickname; }
    public void setReceiverNickname(String receiverNickname) { this.receiverNickname = receiverNickname; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}