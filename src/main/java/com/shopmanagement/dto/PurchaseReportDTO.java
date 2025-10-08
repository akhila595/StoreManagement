package com.shopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseReportDTO {
    private String supplierName;
    private String productName;
    private int quantity;
    private BigDecimal thresholdPrice;
    private LocalDate purchaseDate;

    // Getters & Setters
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getThresholdPrice() { return thresholdPrice; }
    public void setThresholdPrice(BigDecimal thresholdPrice) { this.thresholdPrice = thresholdPrice; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
}
