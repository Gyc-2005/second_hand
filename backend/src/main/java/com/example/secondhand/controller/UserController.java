
package com.example.secondhand.controller;

import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.dto.UserLoginDTO;
import com.example.secondhand.dto.UserRegisterDTO;
import com.example.secondhand.dto.UserUpdateDTO;
import com.example.secondhand.dto.ChangePasswordDTO;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<User>> register(@Valid @RequestBody UserRegisterDTO dto) {
        User user = userService.register(dto);
        return ResponseEntity.ok(ResponseDTO.success("注册成功", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<User>> login(@Valid @RequestBody UserLoginDTO dto, HttpSession session) {
        User user = userService.login(dto);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        return ResponseEntity.ok(ResponseDTO.success("登录成功", user));
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ResponseDTO.success("退出成功", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<User>> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(ResponseDTO.success(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<User>> update(@PathVariable Integer id, @Valid @RequestBody UserUpdateDTO dto) {
        User user = userService.update(id, dto);
        return ResponseEntity.ok(ResponseDTO.success("更新成功", user));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDTO<Void>> changePassword(@Valid @RequestBody ChangePasswordDTO dto,
                                                           HttpSession session,
                                                           @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        userService.changePassword(userId, dto);
        return ResponseEntity.ok(ResponseDTO.success("密码修改成功", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.ok(ResponseDTO.success("删除成功", null));
    }

    @GetMapping("/current")
    public ResponseEntity<ResponseDTO<User>> getCurrent(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        User user = userService.getById(userId);
        return ResponseEntity.ok(ResponseDTO.success(user));
    }

    private Integer getUserId(HttpSession session, Integer headerUserId) {
        if (headerUserId != null) {
            return headerUserId;
        }
        return (Integer) session.getAttribute("userId");
    }
}
