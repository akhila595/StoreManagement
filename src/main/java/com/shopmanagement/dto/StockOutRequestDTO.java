package com.shopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockOutRequestDTO {

    private String sku;
    private int quantity;
    private LocalDateTime saleDate;
    private String remarks;
    private BigDecimal finalPrice;   // after discount if any
    private Long customerId; // ✅ Added

    // Getters and Setters
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
