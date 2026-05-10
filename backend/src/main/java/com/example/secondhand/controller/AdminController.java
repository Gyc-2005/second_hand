package com.example.secondhand.controller;

import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.entity.Order;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.repository.OrderRepository;
import com.example.secondhand.repository.ProductRepository;
import com.example.secondhand.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/users")
    public ResponseEntity<ResponseDTO<List<User>>> getAllUsers(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ResponseDTO.success(users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseDTO<User>> getUserById(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(ResponseDTO.error(404, "用户不存在"));
        }
        return ResponseEntity.ok(ResponseDTO.success(user));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ResponseDTO<User>> updateUserStatus(@PathVariable Integer id, 
                                                              @RequestParam Integer status,
                                                              HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(ResponseDTO.error(404, "用户不存在"));
        }
        user.setStatus(status);
        userRepository.save(user);
        return ResponseEntity.ok(ResponseDTO.success("状态更新成功", user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        if (!userRepository.existsById(id)) {
            return ResponseEntity.ok(ResponseDTO.error(404, "用户不存在"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(ResponseDTO.success("删除成功", null));
    }

    @GetMapping("/products")
    public ResponseEntity<ResponseDTO<List<Product>>> getAllProducts(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        if (!productRepository.existsById(id)) {
            return ResponseEntity.ok(ResponseDTO.error(404, "商品不存在"));
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(ResponseDTO.success("删除成功", null));
    }

    @GetMapping("/orders")
    public ResponseEntity<ResponseDTO<List<Order>>> getAllOrders(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(ResponseDTO.success(orders));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseDTO<Object>> getStats(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.ok(ResponseDTO.error(403, "权限不足"));
        }
        long userCount = userRepository.count();
        long productCount = productRepository.count();
        long orderCount = orderRepository.count();
        
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("userCount", userCount);
        stats.put("productCount", productCount);
        stats.put("orderCount", orderCount);
        
        return ResponseEntity.ok(ResponseDTO.success(stats));
    }

    private boolean isAdmin(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getRole() == 1;
    }
}