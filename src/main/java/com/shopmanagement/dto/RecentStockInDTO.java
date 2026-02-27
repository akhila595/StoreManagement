package com.shopmanagement.dto;

import java.time.LocalDate;

public class RecentStockInDTO {

    private String productName;
    private String sku;
    private int quantityAdded;
    private String supplierName;
    private LocalDate stockInDate;
    private String imageUrl;
    private Long customerId; // tenant context

    // Default constructor
    public RecentStockInDTO() {}

    // Constructor without customerId (optional)
    public RecentStockInDTO(String productName,
                            String sku,
                            int quantityAdded,
                            String supplierName,
                            LocalDate stockInDate,
                            String imageUrl) {
        this.productName = productName;
        this.sku = sku;
        this.quantityAdded = quantityAdded;
        this.supplierName = supplierName;
        this.stockInDate = stockInDate;
        this.imageUrl = imageUrl;
    }

    // Full constructor
    public RecentStockInDTO(String productName,
                            String sku,
                            int quantityAdded,
                            String supplierName,
                            LocalDate stockInDate,
                            String imageUrl,
                            Long customerId) {
        this.productName = productName;
        this.sku = sku;
        this.quantityAdded = quantityAdded;
        this.supplierName = supplierName;
        this.stockInDate = stockInDate;
        this.imageUrl = imageUrl;
        this.customerId = customerId;
    }

    // Getters & Setters

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantityAdded() {
        return quantityAdded;
    }

    public void setQuantityAdded(int quantityAdded) {
        this.quantityAdded = quantityAdded;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getStockInDate() {
        return stockInDate;
    }

    public void setStockInDate(LocalDate stockInDate) {
        this.stockInDate = stockInDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}