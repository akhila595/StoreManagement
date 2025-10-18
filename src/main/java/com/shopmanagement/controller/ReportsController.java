package com.shopmanagement.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.shopmanagement.dto.*;
import com.shopmanagement.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;

    // 1. Daily Report
    @GetMapping("/daily")
    public DetailedDailyReportDTO getDailyReport(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getDailyReport(date);
    }

    // 2. Monthly Report
    @GetMapping("/monthly")
    public DetailedDailyReportDTO getMonthlyReport(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        return reportService.getMonthlyReport(YearMonth.of(year, month));
    }

    // 3. Category Wise Profit/Loss
    @GetMapping("/category")
    public List<CategoryReportDTO> getCategoryWiseReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getCategoryWiseReport(startDate, endDate);
    }

    // 4. Supplier Purchase History
    @GetMapping("/supplier/{supplierId}")
    public List<PurchaseReportDTO> getSupplierPurchaseHistory(
            @PathVariable ("supplierId") Long supplierId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getSupplierPurchaseHistory(supplierId, startDate, endDate);
    }

    // 5. Top Selling Products
    @GetMapping("/top-selling")
    public List<TopSellingProductDTO> getTopSellingProducts(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return reportService.getTopSellingProducts(startDate, endDate, limit);
    }
    
    @GetMapping("/low-stock")
    public List<LowStockProductDTO> getLowStockProducts() {
        return reportService.getLowStockProducts();
    }
    
 // 6. Weekly Report
    @GetMapping("/weekly")
    public DetailedDailyReportDTO getWeeklyReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getWeeklyReport(startDate, endDate);
    }

    // 7. Yearly Report
    @GetMapping("/yearly")
    public DetailedDailyReportDTO getYearlyReport(@RequestParam("year") int year) {
        return reportService.getYearlyReport(year);
    }
    
 // 8. All Suppliers Purchase Report
    @GetMapping("/suppliers")
    public List<PurchaseReportDTO> getAllSuppliersPurchaseReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    	return reportService.getAllSuppliersPurchaseReport(startDate, endDate);
    }

}
