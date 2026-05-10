
package com.example.secondhand.repository;

import com.example.secondhand.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Integer productId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<Review> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") Integer productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.productId = :productId")
    Integer countByProductId(@Param("productId") Integer productId);
}
