package com.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

   
	private Long id; // Corresponds to productId in the entity
    private String productName;
    private String designCode;
    private String pattern;
    
    // Brand fields
    private Long brandId; 
    private String brandName; // Optional: Add brand name if needed
    
    // ClothType fields
    private Long clothTypeId; 
    private String clothTypeName; // Optional: Add cloth type name if needed
    
    // Category fields
    private Long categoryId;
    private String categoryName; // Optional: Add category name if needed

    // Constructor including all fields
    public ProductDTO(Long id, String productName, String designCode, String pattern, 
                      Long brandId, String brandName, 
                      Long clothTypeId, String clothTypeName, 
                      Long categoryId, String categoryName) {
        this.id = id;
        this.productName = productName;
        this.designCode = designCode;
        this.pattern = pattern;
        this.brandId = brandId;
        this.brandName = brandName;
        this.clothTypeId = clothTypeId;
        this.clothTypeName = clothTypeName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
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

}
