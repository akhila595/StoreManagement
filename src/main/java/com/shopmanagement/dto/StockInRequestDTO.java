package com.shopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StockInRequestDTO {

    // Product info
    private Long productId;          // null if new product
    private String productName;      // required if productId is null
    private Long categoryId;
    private Long brandId;
    private String imageUrl;

    // Variant info
    private List<Long> attributeValueIds;  // selected attribute values
    private String sku;                    // optional (auto-generated if null)

    // Stock details
    private Integer quantity;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;

    private BigDecimal taxPerUnit;
    private BigDecimal transportPerUnit;

    private LocalDate purchaseDate;
    private String supplierName;
    private String remarks;
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getBrandId() {
		return brandId;
	}
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public List<Long> getAttributeValueIds() {
		return attributeValueIds;
	}
	public void setAttributeValueIds(List<Long> attributeValueIds) {
		this.attributeValueIds = attributeValueIds;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}
	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public BigDecimal getTaxPerUnit() {
		return taxPerUnit;
	}
	public void setTaxPerUnit(BigDecimal taxPerUnit) {
		this.taxPerUnit = taxPerUnit;
	}
	public BigDecimal getTransportPerUnit() {
		return transportPerUnit;
	}
	public void setTransportPerUnit(BigDecimal transportPerUnit) {
		this.transportPerUnit = transportPerUnit;
	}
	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}