
package com.example.secondhand.service.impl;

import com.example.secondhand.dto.UserLoginDTO;
import com.example.secondhand.dto.UserRegisterDTO;
import com.example.secondhand.dto.UserUpdateDTO;
import com.example.secondhand.dto.ChangePasswordDTO;
import com.example.secondhand.entity.User;
import com.example.secondhand.repository.UserRepository;
import com.example.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User register(UserRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(md5(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());

        return userRepository.save(user);
    }

    @Override
    public User login(UserLoginDTO dto) {
        Optional<User> userOpt = userRepository.findByUsernameOrPhone(dto.getUsername(), dto.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(md5(dto.getPassword()))) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        return user;
    }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional
    public User update(Integer id, UserUpdateDTO dto) {
        User user = getById(id);

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            if (!dto.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(dto.getPhone())) {
                throw new RuntimeException("手机号已被使用");
            }
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, ChangePasswordDTO dto) {
        User user = getById(id);
        
        if (!user.getPassword().equals(md5(dto.getOldPassword()))) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(md5(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        User user = getById(id);
        user.setStatus(0);
        userRepository.save(user);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
