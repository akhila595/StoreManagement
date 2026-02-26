package com.shopmanagement.dto;

public class TopSellingProductDTO {

    private String productName;
    private String brandName;
    private String sku;
    private String attributes;   // Dynamic attribute summary (Red / XL / Cotton etc.)
    private Integer quantitySold;
    private String imageUrl;
    private Long customerId;

    public TopSellingProductDTO() {}

    public TopSellingProductDTO(String productName,
                                String brandName,
                                String sku,
                                String attributes,
                                Integer quantitySold,
                                String imageUrl,
                                Long customerId) {

        this.productName = productName;
        this.brandName = brandName;
        this.sku = sku;
        this.attributes = attributes;
        this.quantitySold = quantitySold;
        this.imageUrl = imageUrl;
        this.customerId = customerId;
    }

    // Getters & Setters

    public String getProductName() {
        return productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getSku() {
        return sku;
    }

    public String getAttributes() {
        return attributes;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}