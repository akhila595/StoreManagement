package com.shopmanagement.dto;

import java.math.BigDecimal;

public class ProductSaleReportDTO {

    private String productName;   // e.g., design code
    private String sku;
    private int quantity;
    private BigDecimal saleTotal;
    private BigDecimal costTotal;
    private BigDecimal profit;
    private BigDecimal loss;
    private Long customerId;      // ✅ Added for tenant separation

    // Getters & Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getSaleTotal() { return saleTotal; }
    public void setSaleTotal(BigDecimal saleTotal) { this.saleTotal = saleTotal; }

    public BigDecimal getCostTotal() { return costTotal; }
    public void setCostTotal(BigDecimal costTotal) { this.costTotal = costTotal; }

    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }

    public BigDecimal getLoss() { return loss; }
    public void setLoss(BigDecimal loss) { this.loss = loss; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
