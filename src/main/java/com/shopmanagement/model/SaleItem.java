package com.shopmanagement.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "sale_item")
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleItemId;

    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Column(nullable = false)
    private BigDecimal thresholdPriceAtSale; // cost price during sale

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private SaleInvoice saleInvoice;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

	public Long getSaleItemId() {
		return saleItemId;
	}

	public void setSaleItemId(Long saleItemId) {
		this.saleItemId = saleItemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public BigDecimal getThresholdPriceAtSale() {
		return thresholdPriceAtSale;
	}

	public void setThresholdPriceAtSale(BigDecimal thresholdPriceAtSale) {
		this.thresholdPriceAtSale = thresholdPriceAtSale;
	}

	public SaleInvoice getSaleInvoice() {
		return saleInvoice;
	}

	public void setSaleInvoice(SaleInvoice saleInvoice) {
		this.saleInvoice = saleInvoice;
	}

	public ProductVariant getProductVariant() {
		return productVariant;
	}

	public void setProductVariant(ProductVariant productVariant) {
		this.productVariant = productVariant;
	}
	/*
	 * Cost Price (Threshold Price) = what you paid supplier.

      Sale Price (Final Price) = what customer paid you.

      Profit if Sale Price > Cost Price.

      Loss if Sale Price < Cost Price.
	 */
}
