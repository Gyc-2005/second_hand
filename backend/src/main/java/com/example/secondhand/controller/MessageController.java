package com.example.secondhand.controller;

import com.example.secondhand.dto.MessageCreateDTO;
import com.example.secondhand.dto.MessageResponseDTO;
import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.entity.Message;
import com.example.secondhand.service.MessageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Message>> create(@Valid @RequestBody MessageCreateDTO dto, 
                                                      HttpSession session,
                                                      @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Message message = messageService.create(userId, dto);
        return ResponseEntity.ok(ResponseDTO.success("发送成功", message));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseDTO<List<Message>>> listByProduct(@PathVariable Integer productId) {
        List<Message> messages = messageService.listByProduct(productId);
        return ResponseEntity.ok(ResponseDTO.success(messages));
    }

    @GetMapping("/receiver")
    public ResponseEntity<ResponseDTO<List<MessageResponseDTO>>> listByReceiver(HttpSession session,
                                                                    @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        List<MessageResponseDTO> messages = messageService.listByReceiverWithNickname(userId);
        return ResponseEntity.ok(ResponseDTO.success(messages));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ResponseDTO<Integer>> countUnread(HttpSession session,
                                                          @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Integer count = messageService.countUnread(userId);
        return ResponseEntity.ok(ResponseDTO.success(count));
    }

    @PutMapping("/read/all")
    public ResponseEntity<ResponseDTO<Void>> markAllAsRead(HttpSession session,
                                                          @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        messageService.markAllAsRead(userId);
        return ResponseEntity.ok(ResponseDTO.success("已全部标记为已读", null));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ResponseDTO<Message>> reply(@PathVariable Integer id,
                                                      @RequestBody String content,
                                                      HttpSession session,
                                                      @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Message message = messageService.reply(userId, id, content.replace("\"", ""));
        return ResponseEntity.ok(ResponseDTO.success("回复成功", message));
    }

    private Integer getUserId(HttpSession session, Integer headerUserId) {
        if (headerUserId != null) {
            return headerUserId;
        }
        return (Integer) session.getAttribute("userId");
    }
}
