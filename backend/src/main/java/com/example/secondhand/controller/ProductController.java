package com.example.secondhand.controller;

import com.example.secondhand.dto.ProductCreateDTO;
import com.example.secondhand.dto.ProductQueryDTO;
import com.example.secondhand.dto.ProductUpdateDTO;
import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.entity.Product;
import com.example.secondhand.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Product>> create(@Valid @RequestBody ProductCreateDTO dto, 
                                                       HttpSession session,
                                                       @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Product product = productService.create(userId, dto);
        return ResponseEntity.ok(ResponseDTO.success("发布成功", product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Product>> getById(@PathVariable Integer id) {
        productService.incrementViews(id);
        Product product = productService.getById(id);
        return ResponseEntity.ok(ResponseDTO.success(product));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<Page<Product>>> list(ProductQueryDTO dto) {
        Page<Product> products = productService.list(dto);
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Product>> update(@PathVariable Integer id, 
                                                       @Valid @RequestBody ProductUpdateDTO dto,
                                                       HttpSession session,
                                                       @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        Product product = productService.update(id, dto);
        return ResponseEntity.ok(ResponseDTO.success("更新成功", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable Integer id,
                                                    HttpSession session,
                                                    @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        productService.delete(id);
        return ResponseEntity.ok(ResponseDTO.success("删除成功", null));
    }

    @PutMapping("/{id}/offline")
    public ResponseEntity<ResponseDTO<Void>> offline(@PathVariable Integer id,
                                                     HttpSession session,
                                                     @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        productService.offline(id);
        return ResponseEntity.ok(ResponseDTO.success("下架成功", null));
    }

    @PutMapping("/{id}/online")
    public ResponseEntity<ResponseDTO<Void>> online(@PathVariable Integer id,
                                                    HttpSession session,
                                                    @RequestHeader(value = "X-User-Id", required = false) Integer headerUserId) {
        Integer userId = getUserId(session, headerUserId);
        if (userId == null) {
            return ResponseEntity.ok(ResponseDTO.error(401, "请先登录"));
        }
        productService.online(id);
        return ResponseEntity.ok(ResponseDTO.success("上架成功", null));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ResponseDTO<List<Product>>> listBySeller(@PathVariable Integer sellerId) {
        List<Product> products = productService.listBySeller(sellerId);
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    private Integer getUserId(HttpSession session, Integer headerUserId) {
        if (headerUserId != null) {
            return headerUserId;
        }
        return (Integer) session.getAttribute("userId");
    }
}
