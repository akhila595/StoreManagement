package com.shopmanagement.dto;

import java.math.BigDecimal;

public class CategoryReportDTO {
    private String categoryName;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;
    private BigDecimal loss = BigDecimal.ZERO;

    public CategoryReportDTO(String categoryName) {
        this.categoryName = categoryName;
    }

    public void addSale(BigDecimal saleTotal, BigDecimal costTotal) {
        this.totalSales = this.totalSales.add(saleTotal);
        this.totalCost = this.totalCost.add(costTotal);

        BigDecimal diff = saleTotal.subtract(costTotal);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            this.profit = this.profit.add(diff);
        } else {
            this.loss = this.loss.add(diff.abs());
        }
    }

    // Getters & Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }

    public BigDecimal getLoss() { return loss; }
    public void setLoss(BigDecimal loss) { this.loss = loss; }
}
