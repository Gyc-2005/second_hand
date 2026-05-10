package com.example.secondhand.controller;

import com.example.secondhand.dto.OrderCreateDTO;
import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.entity.Order;
import com.example.secondhand.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Order>> create(@Valid @RequestBody OrderCreateDTO dto, 
                                                     HttpSession session,
                                                     @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Order order = orderService.create(userId, dto);
        return ResponseEntity.ok(ResponseDTO.success("下单成功", order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Order>> getById(@PathVariable String id) {
        Order order = orderService.getById(id);
        return ResponseEntity.ok(ResponseDTO.success(order));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDTO<List<Order>>> listByUser(HttpSession session,
                                                               @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        List<Order> orders = orderService.listByUser(userId);
        return ResponseEntity.ok(ResponseDTO.success(orders));
    }

    @GetMapping("/seller")
    public ResponseEntity<ResponseDTO<List<Order>>> listBySeller(HttpSession session,
                                                                @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        List<Order> orders = orderService.listBySeller(userId);
        return ResponseEntity.ok(ResponseDTO.success(orders));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<ResponseDTO<Order>> pay(@PathVariable String id, 
                                                  @RequestParam String paymentMethod,
                                                  HttpSession session,
                                                  @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Order order = orderService.updateStatus(id, 2, paymentMethod);
        return ResponseEntity.ok(ResponseDTO.success("支付成功", order));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<ResponseDTO<Order>> ship(@PathVariable String id,
                                                   HttpSession session,
                                                   @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Order order = orderService.updateStatus(id, 3, null);
        return ResponseEntity.ok(ResponseDTO.success("发货成功", order));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ResponseDTO<Order>> complete(@PathVariable String id,
                                                      HttpSession session,
                                                      @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Order order = orderService.updateStatus(id, 4, null);
        return ResponseEntity.ok(ResponseDTO.success("交易完成", order));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ResponseDTO<Void>> cancel(@PathVariable String id,
                                                    HttpSession session,
                                                    @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        orderService.cancel(id);
        return ResponseEntity.ok(ResponseDTO.success("取消成功", null));
    }

    private Integer getUserId(HttpSession session, Integer headerUserId) {
        if (headerUserId != null) {
            return headerUserId;
        }
        return (Integer) session.getAttribute("userId");
    }
}
