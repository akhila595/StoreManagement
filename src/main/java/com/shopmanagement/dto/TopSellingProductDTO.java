package com.shopmanagement.dto;

public class TopSellingProductDTO {
	private String productName;
	private int quantitySold;
	private String imageUrl;

	public TopSellingProductDTO(String productName, String imageUrl, int quantitySold) {
		this.productName = productName;
		this.imageUrl = imageUrl;
		this.quantitySold = quantitySold;
	}

	// Getters & Setters
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantitySold() {
		return quantitySold;
	}

	public void setQuantitySold(int quantitySold) {
		this.quantitySold = quantitySold;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
