package com.shopmanagement.dto;

import java.math.BigDecimal;

public class ProductVariantDTO {

	private Long variantId;
	private String sku;
	private BigDecimal sellingPrice;
	private BigDecimal costPrice;
	private Integer stockQty;
	private String variantLabel; // 👈 new field

	public ProductVariantDTO() {
	}

	public ProductVariantDTO(Long variantId, String sku, BigDecimal sellingPrice, BigDecimal costPrice,
			String variantLabel, Integer stockQty) {
		this.variantId = variantId;
		this.sku = sku;
		this.sellingPrice = sellingPrice;
		this.costPrice = costPrice;
		this.stockQty = stockQty;
		this.variantLabel = variantLabel;
	}

	public ProductVariantDTO(Long variantId, String sku, BigDecimal sellingPrice, BigDecimal costPrice,
			Integer stockQty) {
		this.variantId = variantId;
		this.sku = sku;
		this.sellingPrice = sellingPrice;
		this.costPrice = costPrice;
		this.stockQty = stockQty;

	}

	public Long getVariantId() {
		return variantId;
	}

	public void setVariantId(Long variantId) {
		this.variantId = variantId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}

	public Integer getStockQty() {
		return stockQty;
	}

	public String getVariantLabel() {
		return variantLabel;
	}

	public void setVariantLabel(String variantLabel) {
		this.variantLabel = variantLabel;
	}

	public void setStockQty(Integer stockQty) {
		this.stockQty = stockQty;
	}
}