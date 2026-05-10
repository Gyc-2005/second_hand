package com.example.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageCreateDTO {

    @NotNull(message = "商品ID不能为空")
    private Integer productId;

    @NotNull(message = "接收者ID不能为空")
    private Integer receiverId;

    @NotBlank(message = "留言内容不能为空")
    private String content;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
