package com.shopmanagement.dto;

import java.math.BigDecimal;

public class StockInRequestDTO {

	private Long categoryId;
	private Long brandId;
	private Long clothTypeId;
	private Long colorId;
	private Long sizeId;

	private String designCode;
	private String pattern;
	private String sku;
	private int quantity;

	private BigDecimal basePrice;
	private BigDecimal taxPerUnit;
	private BigDecimal transportPerUnit;
	private BigDecimal sellingPrice;

	private String purchaseDate; // yyyy-MM-dd
	private String supplierName;
	private String remarks;
	private String productName;

	private String imageUrl;

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

	public Long getClothTypeId() {
		return clothTypeId;
	}

	public void setClothTypeId(Long clothTypeId) {
		this.clothTypeId = clothTypeId;
	}

	public Long getColorId() {
		return colorId;
	}

	public void setColorId(Long colorId) {
		this.colorId = colorId;
	}

	public Long getSizeId() {
		return sizeId;
	}

	public void setSizeId(Long sizeId) {
		this.sizeId = sizeId;
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
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

	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
