
package com.example.secondhand.service;

import com.example.secondhand.dto.UserLoginDTO;
import com.example.secondhand.dto.UserRegisterDTO;
import com.example.secondhand.dto.UserUpdateDTO;
import com.example.secondhand.dto.ChangePasswordDTO;
import com.example.secondhand.entity.User;

public interface UserService {

    User register(UserRegisterDTO dto);

    User login(UserLoginDTO dto);

    User getById(Integer id);

    User update(Integer id, UserUpdateDTO dto);

    void changePassword(Integer id, ChangePasswordDTO dto);

    void delete(Integer id);
}
