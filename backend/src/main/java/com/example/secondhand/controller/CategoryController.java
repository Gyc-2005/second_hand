
package com.example.secondhand.controller;

import com.example.secondhand.dto.ResponseDTO;
import com.example.secondhand.entity.Category;
import com.example.secondhand.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<Category>>> listAll() {
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        return ResponseEntity.ok(ResponseDTO.success(categories));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ResponseDTO<List<Category>>> listByParent(@PathVariable Integer parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        return ResponseEntity.ok(ResponseDTO.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Category>> getById(@PathVariable Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        return ResponseEntity.ok(ResponseDTO.success(category));
    }
}
