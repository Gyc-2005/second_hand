
package com.example.secondhand.repository;

import com.example.secondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByStatus(Integer status, Pageable pageable);

    Page<Product> findByCategoryIdAndStatus(Integer categoryId, Integer status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND p.stock > 0 AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByKeywordWithStock(@Param("keyword") String keyword, Pageable pageable);

    List<Product> findBySellerId(Integer sellerId);

    List<Product> findBySellerIdAndStatus(Integer sellerId, Integer status);

    @Modifying
    @Query("UPDATE Product p SET p.views = p.views + 1 WHERE p.id = :id")
    void incrementViews(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity, p.status = CASE WHEN p.stock - :quantity <= 0 THEN 2 ELSE p.status END WHERE p.id = :id AND p.stock >= :quantity")
    int updateStockAndStatus(@Param("id") Integer id, @Param("quantity") Integer quantity);

    Page<Product> findByCategoryIdAndStatusAndStockGreaterThan(Integer categoryId, Integer status, Integer stock, Pageable pageable);

    Page<Product> findByStatusAndStockGreaterThan(Integer status, Integer stock, Pageable pageable);
}
