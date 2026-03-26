package com.shopmanagement.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopmanagement.dto.*;
import com.shopmanagement.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

	private final ReportService reportService;

	public ReportsController(ReportService reportService) {
		this.reportService = reportService;
	}

	/*
	 * ========================================================= DAILY REPORT
	 * =========================================================
	 */

	@GetMapping("/daily")
	public ResponseEntity<DetailedDailyReportDTO> getDailyReport(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return ResponseEntity.ok(reportService.getDailyReport(date));
	}

	/*
	 * ========================================================= MONTHLY REPORT
	 * =========================================================
	 */

	@GetMapping("/monthly")
	public ResponseEntity<DetailedDailyReportDTO> getMonthlyReport(@RequestParam("year") int year,
			@RequestParam("month") int month) {

		return ResponseEntity.ok(reportService.getMonthlyReport(YearMonth.of(year, month)));
	}

	/*
	 * ========================================================= CATEGORY REPORT
	 * =========================================================
	 */

	@GetMapping("/category")
	public ResponseEntity<List<CategoryReportDTO>> getCategoryWiseReport(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		return ResponseEntity.ok(reportService.getCategoryWiseReport(startDate, endDate));
	}

	/*
	 * ========================================================= TOP SELLING
	 * PRODUCTS =========================================================
	 */

	@GetMapping("/top-selling")
	public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProducts(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

			@RequestParam(value = "limit", defaultValue = "5") int limit) {

		return ResponseEntity.ok(reportService.getTopSellingProducts(startDate, endDate, limit));
	}

	/*
	 * ========================================================= LOW STOCK PRODUCTS
	 * =========================================================
	 */

	@GetMapping("/low-stock")
	public ResponseEntity<List<LowStockProductDTO>> getLowStockProducts(
			@RequestParam(value = "threshold", defaultValue = "10") int threshold) {

		return ResponseEntity.ok(reportService.getLowStockProducts(threshold));
	}

	/*
	 * ========================================================= PURCHASE REPORT
	 * (All Suppliers) =========================================================
	 */

	@GetMapping("/purchases")
	public ResponseEntity<List<PurchaseReportDTO>> getPurchaseReport(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		return ResponseEntity.ok(reportService.getPurchaseReport(startDate, endDate));
	}

	/*
	 * ========================================================= YEARLY REPORT
	 */

	@GetMapping("/yearly")
	public ResponseEntity<DetailedDailyReportDTO> getYearlyReport(@RequestParam("year") int year) {

		return ResponseEntity.ok(reportService.getYearlyReport(year));
	}

	@GetMapping("/weekly")
	public ResponseEntity<DetailedDailyReportDTO> getWeeklyReport(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseEntity.ok( reportService.getWeeklyReport(startDate, endDate));
	}

}