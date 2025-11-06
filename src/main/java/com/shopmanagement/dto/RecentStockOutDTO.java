package com.shopmanagement.dto;

import java.time.LocalDateTime;

public class RecentStockOutDTO {
    private String productName;
    private String sku;
    private int quantityRemoved;
    private String reason;
    private LocalDateTime stockOutDate;
    private String imageUrl;

    public RecentStockOutDTO(String productName, String sku, int quantityRemoved, String reason, LocalDateTime stockOutDate, String imageUrl) {
        this.productName = productName;
        this.sku = sku;
        this.quantityRemoved = quantityRemoved;
        this.reason = reason;
        this.stockOutDate = stockOutDate;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantityRemoved() { return quantityRemoved; }
    public void setQuantityRemoved(int quantityRemoved) { this.quantityRemoved = quantityRemoved; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getStockOutDate() { return stockOutDate; }
    public void setStockOutDate(LocalDateTime stockOutDate) { this.stockOutDate = stockOutDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
