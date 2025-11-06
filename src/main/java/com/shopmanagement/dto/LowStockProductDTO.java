package com.shopmanagement.dto;

public class LowStockProductDTO {

    private String productName;
    private String sku;     // ✅ Added
    private Integer stockQty;

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {   // ✅ Added
        return sku;
    }
    public void setSku(String sku) {   // ✅ Added
        this.sku = sku;
    }

    public Integer getStockQty() {
        return stockQty;
    }
    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }
}