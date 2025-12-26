package com.shopmanagement.dto;

import java.time.LocalDateTime;

public class RecentStockOutDTO {

    private String productName;
    private String sku;
    private int quantityRemoved;
    private String reason;
    private LocalDateTime stockOutDate;
    private String imageUrl;
    private Long customerId; // ✅ Added field for tenant context

    // ✅ Default constructor (for frameworks)
    public RecentStockOutDTO() {}

    // ✅ Constructor without customerId (for compatibility)
    public RecentStockOutDTO(String productName, String sku, int quantityRemoved, String reason,
                             LocalDateTime stockOutDate, String imageUrl) {
        this.productName = productName;
        this.sku = sku;
        this.quantityRemoved = quantityRemoved;
        this.reason = reason;
        this.stockOutDate = stockOutDate;
        this.imageUrl = imageUrl;
    }

    // ✅ Full constructor (with customerId)
    public RecentStockOutDTO(String productName, String sku, int quantityRemoved, String reason,
                             LocalDateTime stockOutDate, String imageUrl, Long customerId) {
        this.productName = productName;
        this.sku = sku;
        this.quantityRemoved = quantityRemoved;
        this.reason = reason;
        this.stockOutDate = stockOutDate;
        this.imageUrl = imageUrl;
        this.customerId = customerId;
    }

    // ✅ Getters and Setters
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

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
