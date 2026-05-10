
package com.example.secondhand.service.impl;

import com.example.secondhand.dto.ReviewCreateDTO;
import com.example.secondhand.entity.Order;
import com.example.secondhand.entity.Review;
import com.example.secondhand.repository.OrderRepository;
import com.example.secondhand.repository.ReviewRepository;
import com.example.secondhand.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public Review create(ReviewCreateDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != 4) {
            throw new RuntimeException("订单未完成，无法评价");
        }

        if (reviewRepository.existsByOrderId(dto.getOrderId())) {
            throw new RuntimeException("该订单已评价");
        }

        Review review = new Review();
        review.setOrderId(dto.getOrderId());
        review.setUserId(order.getUserId());
        review.setProductId(order.getProductId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> listByProduct(Integer productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<Review> listByUser(Integer userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Map<String, Object> getProductRating(Integer productId) {
        Map<String, Object> result = new HashMap<>();
        result.put("avg_rating", reviewRepository.findAverageRatingByProductId(productId));
        result.put("review_count", reviewRepository.countByProductId(productId));
        return result;
    }
}
