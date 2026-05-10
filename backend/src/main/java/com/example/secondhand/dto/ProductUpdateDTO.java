package com.example.secondhand.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ProductUpdateDTO {

    private String name;

    private String description;

    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer categoryId;

    private String images;

    @Positive(message = "库存数量必须大于0")
    private Integer stock;

    private Integer status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
