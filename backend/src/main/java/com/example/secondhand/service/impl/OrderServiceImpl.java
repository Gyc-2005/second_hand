
package com.example.secondhand.service.impl;

import com.example.secondhand.dto.OrderCreateDTO;
import com.example.secondhand.entity.Order;
import com.example.secondhand.entity.Product;
import com.example.secondhand.repository.OrderRepository;
import com.example.secondhand.repository.ProductRepository;
import com.example.secondhand.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Order create(Integer userId, OrderCreateDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架或已卖出");
        }
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("库存不足");
        }
        if (product.getSellerId().equals(userId)) {
            throw new RuntimeException("不能购买自己的商品");
        }

        Order order = new Order();
        order.setId(generateOrderId());
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setSellerId(product.getSellerId());
        order.setPrice(product.getPrice());
        order.setQuantity(dto.getQuantity());
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        order.setShippingAddress(dto.getShippingAddress());

        Order savedOrder = orderRepository.save(order);

        int updated = productRepository.updateStockAndStatus(product.getId(), dto.getQuantity());
        if (updated == 0) {
            throw new RuntimeException("下单失败，库存不足");
        }

        return savedOrder;
    }

    @Override
    public Order getById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    public List<Order> listByUser(Integer userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Order> listBySeller(Integer sellerId) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }

    @Override
    @Transactional
    public Order updateStatus(String id, Integer status, String paymentMethod) {
        Order order = getById(id);

        if (status == 2) {
            if (order.getStatus() != 1) {
                throw new RuntimeException("订单状态不正确，无法支付");
            }
            order.setStatus(2);
            order.setPaymentMethod(paymentMethod);
            order.setPaymentTime(LocalDateTime.now());
        } else if (status == 3) {
            if (order.getStatus() != 2) {
                throw new RuntimeException("订单状态不正确，无法发货");
            }
            order.setStatus(3);
        } else if (status == 4) {
            if (order.getStatus() != 3) {
                throw new RuntimeException("订单状态不正确，无法完成");
            }
            order.setStatus(4);
        } else {
            throw new RuntimeException("不支持的状态变更");
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        Order order = getById(id);
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确，无法取消");
        }
        order.setStatus(5);
        orderRepository.save(order);
    }

    private String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ORD" + timestamp + uuid;
    }
}
