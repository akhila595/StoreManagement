package com.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product
 * Used for sending and receiving product data across API.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    // --- Product fields ---
    private Long id;              // Product ID
    private String productName;   // Product name
    private String designCode;    // Unique design code
    private String pattern;       // Product pattern
    private String imageUrl;      // Image URL (optional)

    // --- Brand fields ---
    private Long brandId;         // Related Brand ID
    private String brandName;     // Read-only Brand name

    // --- ClothType fields ---
    private Long clothTypeId;     // Related ClothType ID
    private String clothTypeName; // Read-only ClothType name

    // --- Category fields ---
    private Long categoryId;      // Related Category ID
    private String categoryName;  // Read-only Category name

    // --- Customer field ---
    private Long customerId;      // ✅ Added for multi-customer reference

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDesignCode() {
		return designCode;
	}

	public void setDesignCode(String designCode) {
		this.designCode = designCode;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Long getClothTypeId() {
		return clothTypeId;
	}

	public void setClothTypeId(Long clothTypeId) {
		this.clothTypeId = clothTypeId;
	}

	public String getClothTypeName() {
		return clothTypeName;
	}

	public void setClothTypeName(String clothTypeName) {
		this.clothTypeName = clothTypeName;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
}
