package com.shopmanagement.dto;

public class TopSellingProductDTO {

    private String productName;
    private String brandName;
    private String pattern;
    private String clothType;
    private String color;
    private String size;
    private Integer quantitySold;
    private String imageUrl;
    private Long customerId; // ✅ Added for tenant context

    public TopSellingProductDTO(String productName, String brandName, String pattern, String clothType,
                                String color, String size, Integer quantitySold, String imageUrl, Long customerId) {
        this.productName = productName;
        this.brandName = brandName;
        this.pattern = pattern;
        this.clothType = clothType;
        this.color = color;
        this.size = size;
        this.quantitySold = quantitySold;
        this.imageUrl = imageUrl;
        this.customerId = customerId;
    }

    // Getters & Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public String getClothType() { return clothType; }
    public void setClothType(String clothType) { this.clothType = clothType; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
