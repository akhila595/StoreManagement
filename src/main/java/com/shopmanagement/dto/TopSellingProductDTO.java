package com.shopmanagement.dto;

public class TopSellingProductDTO {
    private String productName;
    private int quantitySold;

    public TopSellingProductDTO(String productName, int quantitySold) {
        this.productName = productName;
        this.quantitySold = quantitySold;
    }

    // Getters & Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
}
