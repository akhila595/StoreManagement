package com.shopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockOutRequestDTO {

    private String sku;        // barcode scanning friendly
    // OR alternatively: private Long variantId;

    private Integer quantity;
    private LocalDateTime saleDate;
    private BigDecimal finalPrice;
    private String remarks;
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
	public LocalDateTime getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(LocalDateTime saleDate) {
		this.saleDate = saleDate;
	}
	public BigDecimal getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}