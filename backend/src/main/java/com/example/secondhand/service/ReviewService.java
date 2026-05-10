
package com.example.secondhand.service;

import com.example.secondhand.dto.ReviewCreateDTO;
import com.example.secondhand.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    Review create(ReviewCreateDTO dto);

    List<Review> listByProduct(Integer productId);

    List<Review> listByUser(Integer userId);

    Map<String, Object> getProductRating(Integer productId);
}
