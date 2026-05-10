
package com.example.secondhand.service;

import com.example.secondhand.dto.ProductCreateDTO;
import com.example.secondhand.dto.ProductQueryDTO;
import com.example.secondhand.dto.ProductUpdateDTO;
import com.example.secondhand.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product create(Integer sellerId, ProductCreateDTO dto);

    Product getById(Integer id);

    Page<Product> list(ProductQueryDTO dto);

    Product update(Integer id, ProductUpdateDTO dto);

    void delete(Integer id);

    void offline(Integer id);

    void online(Integer id);

    List<Product> listBySeller(Integer sellerId);

    void incrementViews(Integer id);
}
