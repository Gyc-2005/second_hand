
package com.example.secondhand.repository;

import com.example.secondhand.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserId(Integer userId);

    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Order> findBySellerId(Integer sellerId);

    List<Order> findBySellerIdOrderByCreatedAtDesc(Integer sellerId);

    List<Order> findByUserIdAndStatus(Integer userId, Integer status);

    Optional<Order> findByIdAndUserId(String id, Integer userId);

    boolean existsByProductIdAndUserIdAndStatus(Integer productId, Integer userId, Integer status);
}
