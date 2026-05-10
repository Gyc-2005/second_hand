
package com.example.secondhand.service;

import com.example.secondhand.dto.OrderCreateDTO;
import com.example.secondhand.entity.Order;

import java.util.List;

public interface OrderService {

    Order create(Integer userId, OrderCreateDTO dto);

    Order getById(String id);

    List<Order> listByUser(Integer userId);

    List<Order> listBySeller(Integer sellerId);

    Order updateStatus(String id, Integer status, String paymentMethod);

    void cancel(String id);
}
