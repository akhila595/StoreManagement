package com.shopmanagement.dto;

import java.time.LocalDate;

public class RecentStockInDTO {

    private String productName;
    private String sku;
    private int quantityAdded;
    private String supplierName;
    private LocalDate stockInDate;
    private String imageUrl;

    // ✅ Exact constructor matching your usage
    public RecentStockInDTO(String productName, String sku, int quantityAdded,
                            String supplierName, LocalDate stockInDate, String imageUrl) {
        this.productName = productName;
        this.sku = sku;
        this.quantityAdded = quantityAdded;
        this.supplierName = supplierName;
        this.stockInDate = stockInDate;
        this.imageUrl = imageUrl;
    }

    // ✅ Getters and Setters
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
}
