package com.amnii.ShopSmart.DTO;

import org.springframework.web.multipart.MultipartFile;

public class ProductDTO {
    private String name;
    private String category;
    private Integer stockQuantity;
    private String unit;
    private Double costPrice;
    private Double sellingPrice;
    private String supplier;
    private Integer stockAlertLevel;
    private MultipartFile image;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }
    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public Integer getStockAlertLevel() { return stockAlertLevel; }
    public void setStockAlertLevel(Integer stockAlertLevel) { this.stockAlertLevel = stockAlertLevel; }
    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}