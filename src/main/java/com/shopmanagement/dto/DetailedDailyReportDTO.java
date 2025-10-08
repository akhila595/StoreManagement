package com.shopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class DetailedDailyReportDTO {
    private LocalDate date;                // For daily report
    private YearMonth month;               // For monthly report
    private Integer year;                  // For yearly report

    private List<ProductSaleReportDTO> productSales; // per-product breakdown
    private BigDecimal totalSales;
    private BigDecimal totalProfit;
    private BigDecimal totalLoss;
    private int totalQuantitySold;         // Add this field to track the total quantity sold
    private LocalDate startDate;
    private LocalDate endDate;
    public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	
    // Getters & Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<ProductSaleReportDTO> getProductSales() {
        return productSales;
    }

    public void setProductSales(List<ProductSaleReportDTO> productSales) {
        this.productSales = productSales;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getTotalLoss() {
        return totalLoss;
    }

    public void setTotalLoss(BigDecimal totalLoss) {
        this.totalLoss = totalLoss;
    }

    public int getTotalQuantitySold() {  // Getter for totalQuantitySold
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(int totalQuantitySold) {  // Setter for totalQuantitySold
        this.totalQuantitySold = totalQuantitySold;
    }
}
