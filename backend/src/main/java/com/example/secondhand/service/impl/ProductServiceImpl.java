
package com.example.secondhand.service.impl;

import com.example.secondhand.dto.ProductCreateDTO;
import com.example.secondhand.dto.ProductQueryDTO;
import com.example.secondhand.dto.ProductUpdateDTO;
import com.example.secondhand.entity.Product;
import com.example.secondhand.repository.ProductRepository;
import com.example.secondhand.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Product create(Integer sellerId, ProductCreateDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setCategoryId(dto.getCategoryId());
        product.setSellerId(sellerId);
        product.setImages(dto.getImages());
        product.setStock(dto.getStock() != null ? dto.getStock() : 1);
        product.setStatus(1);

        return productRepository.save(product);
    }

    @Override
    public Product getById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
    }

    @Override
    public Page<Product> list(ProductQueryDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            return productRepository.searchByKeywordWithStock(dto.getKeyword(), pageable);
        }

        if (dto.getCategoryId() != null) {
            return productRepository.findByCategoryIdAndStatusAndStockGreaterThan(dto.getCategoryId(), 1, 0, pageable);
        }

        return productRepository.findByStatusAndStockGreaterThan(1, 0, pageable);
    }

    @Override
    @Transactional
    public Product update(Integer id, ProductUpdateDTO dto) {
        Product product = getById(id);

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getOriginalPrice() != null) {
            product.setOriginalPrice(dto.getOriginalPrice());
        }
        if (dto.getCategoryId() != null) {
            product.setCategoryId(dto.getCategoryId());
        }
        if (dto.getImages() != null) {
            product.setImages(dto.getImages());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Product product = getById(id);
        product.setStatus(3);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void offline(Integer id) {
        Product product = getById(id);
        product.setStatus(2);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void online(Integer id) {
        Product product = getById(id);
        product.setStatus(1);
        productRepository.save(product);
    }

    @Override
    public List<Product> listBySeller(Integer sellerId) {
        return productRepository.findBySellerIdAndStatus(sellerId, 1);
    }

    @Override
    @Transactional
    public void incrementViews(Integer id) {
        productRepository.incrementViews(id);
    }
}
