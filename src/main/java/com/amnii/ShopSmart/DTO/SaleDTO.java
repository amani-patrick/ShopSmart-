package com.amnii.ShopSmart.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SaleDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Positive(message = "Quantity sold must be positive")
    private int quantitySold;

    @Positive(message = "Total amount must be positive")
    private double totalAmount;

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // ... (omitted for brevity)
} 