
package com.example.secondhand.controller;

import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.dto.ReviewCreateDTO;
import com.example.secondhand.entity.Review;
import com.example.secondhand.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Review>> create(@Valid @RequestBody ReviewCreateDTO dto) {
        Review review = reviewService.create(dto);
        return ResponseEntity.ok(ResponseDTO.success("评价成功", review));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseDTO<List<Review>>> listByProduct(@PathVariable Integer productId) {
        List<Review> reviews = reviewService.listByProduct(productId);
        return ResponseEntity.ok(ResponseDTO.success(reviews));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<Review>>> listByUser(@PathVariable Integer userId) {
        List<Review> reviews = reviewService.listByUser(userId);
        return ResponseEntity.ok(ResponseDTO.success(reviews));
    }

    @GetMapping("/rating/{productId}")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getProductRating(@PathVariable Integer productId) {
        Map<String, Object> rating = reviewService.getProductRating(productId);
        return ResponseEntity.ok(ResponseDTO.success(rating));
    }
}
