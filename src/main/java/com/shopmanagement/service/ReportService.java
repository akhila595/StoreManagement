package com.shopmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopmanagement.dto.*;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

@Service
public class ReportService {

	@Autowired
	private SaleItemRepository saleItemRepo;
	@Autowired
	private PurchaseRepository purchaseRepo;
	@Autowired
	private ProductVariantRepository variantRepo;
	@Autowired
	private VariantAttributeRepository variantAttributeRepo;
	@Autowired
	private JwtUtils jwtUtils;

	/*
	 * ==========================================================
	 * ===================== DAILY REPORT =======================
	 * ==========================================================
	 */

	public DetailedDailyReportDTO getDailyReport(LocalDate date) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<SaleItem> sales =saleItemRepo.findBySaleInvoice_SaleDateAndCustomer_IdAndSaleInvoice_Status(date, customerId, "ACTIVE");
		
		
		return calculateReport(sales, date, customerId);
	}

	/*
	 * ==========================================================
	 * ===================== MONTHLY REPORT =====================
	 * ==========================================================
	 */

	public DetailedDailyReportDTO getMonthlyReport(YearMonth month) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndSaleInvoice_Status(month.atDay(1),
				month.atEndOfMonth(), customerId, "ACTIVE");

		DetailedDailyReportDTO report = calculateReport(sales, null, customerId);
		report.setMonth(month);
		return report;
	}

	/*
	 * ==========================================================
	 * ===================== TOP SELLING PRODUCTS ===============
	 * "Which variants sold the most in a given period"
	 * ==========================================================
	 */

	public List<TopSellingProductDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndSaleInvoice_Status(startDate, endDate,
				customerId, "ACTIVE");

		Map<ProductVariant, Integer> variantQtyMap = new HashMap<>();

		for (SaleItem sale : sales) {

			ProductVariant variant = sale.getProductVariant();
			if (variant == null)
				continue;

			Product product = variant.getProduct();
			if (product == null || "DELETED".equals(product.getStatus()))
				continue;

			variantQtyMap.put(variant, variantQtyMap.getOrDefault(variant, 0) + sale.getQuantity());
		}

		return variantQtyMap.entrySet().stream().sorted((a, b) -> b.getValue().compareTo(a.getValue())).limit(limit)
				.map(entry -> {

					ProductVariant variant = entry.getKey();
					Product product = variant.getProduct();

					String attributeSummary = getVariantAttributeSummary(variant, customerId);

					return new TopSellingProductDTO(product.getName(),
							product.getBrand() != null ? product.getBrand().getBrand() : "-", variant.getProductSku(),
							attributeSummary, entry.getValue(), product.getImageUrl(), customerId);
				}).collect(Collectors.toList());
	}

	/*
	 * ==========================================================
	 * ===================== LOW STOCK PRODUCTS =================
	 * ==========================================================
	 */

	public List<LowStockProductDTO> getLowStockProducts(int threshold) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<ProductVariant> variants = variantRepo.findByStockQtyLessThanAndCustomer_Id(threshold, customerId);

		List<LowStockProductDTO> result = new ArrayList<>();

		for (ProductVariant variant : variants) {

			if (variant == null)
				continue;

			Product product = variant.getProduct();
			if (product == null || "DELETED".equals(product.getStatus()))
				continue;

			LowStockProductDTO dto = new LowStockProductDTO();
			dto.setProductName(product.getName());
			dto.setSku(variant.getProductSku());
			dto.setStockQty(variant.getStockQty());
			dto.setCustomerId(customerId);

			result.add(dto);
		}

		return result;
	}

	/*
	 * ==========================================================
	 * ===================== CATEGORY REPORT ====================
	 * ==========================================================
	 */

	public List<CategoryReportDTO> getCategoryWiseReport(LocalDate startDate, LocalDate endDate) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndSaleInvoice_Status(startDate, endDate,
				customerId, "ACTIVE");

		Map<String, CategoryReportDTO> categoryMap = new HashMap<>();

		for (SaleItem sale : sales) {

			ProductVariant variant = sale.getProductVariant();
			if (variant == null)
				continue;

			Product product = variant.getProduct();
			if (product == null || "DELETED".equals(product.getStatus()) || product.getCategory() == null)
				continue;

			String categoryName = product.getCategory().getCategoryName();

			CategoryReportDTO report = categoryMap.getOrDefault(categoryName, new CategoryReportDTO(categoryName));

			BigDecimal saleTotal = sale.getFinalPrice().multiply(BigDecimal.valueOf(sale.getQuantity()));

			BigDecimal threshold = Optional.ofNullable(sale.getThresholdPriceAtSale()).orElse(BigDecimal.ZERO);

			BigDecimal costTotal = threshold.multiply(BigDecimal.valueOf(sale.getQuantity()));

			report.addSale(saleTotal, costTotal);
			categoryMap.put(categoryName, report);
		}

		return new ArrayList<>(categoryMap.values());
	}

	/*
	 * ==========================================================
	 * ===================== PURCHASE REPORT ====================
	 * ==========================================================
	 */

	public List<PurchaseReportDTO> getPurchaseReport(LocalDate startDate, LocalDate endDate) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<Purchase> purchases = purchaseRepo.findByPurchaseDateBetweenAndCustomer_IdAndStatus(startDate, endDate,
				customerId, "ACTIVE");

		List<PurchaseReportDTO> result = new ArrayList<>();

		for (Purchase p : purchases) {

			if (p.getProductVariant() == null)
				continue;

			Product product = p.getProductVariant().getProduct();
			if (product == null || "DELETED".equals(product.getStatus()))
				continue;

			PurchaseReportDTO dto = new PurchaseReportDTO();
			dto.setSupplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : "Unknown");

			dto.setProductName(product.getName());
			dto.setSku(p.getProductVariant().getProductSku());
			dto.setQuantity(p.getQuantity());
			dto.setThresholdPrice(p.getThresholdPrice());
			dto.setPurchaseDate(p.getPurchaseDate());
			dto.setCustomerId(customerId);

			result.add(dto);
		}

		return result;
	}

	/*
	 * ==========================================================
	 * ===================== COMMON REPORT LOGIC =================
	 * ==========================================================
	 */

	private DetailedDailyReportDTO calculateReport(List<SaleItem> sales, LocalDate date, Long customerId) {

		BigDecimal totalSales = BigDecimal.ZERO;
		BigDecimal totalProfit = BigDecimal.ZERO;
		BigDecimal totalLoss = BigDecimal.ZERO;
		int totalQty = 0;

		List<ProductSaleReportDTO> productReports = new ArrayList<>();

		for (SaleItem sale : sales) {

			ProductVariant variant = sale.getProductVariant();
			if (variant == null)
				continue;

			Product product = variant.getProduct();
			if (product == null || "DELETED".equals(product.getStatus()))
				continue;

			BigDecimal saleTotal = sale.getFinalPrice().multiply(BigDecimal.valueOf(sale.getQuantity()));

			BigDecimal threshold = Optional.ofNullable(sale.getThresholdPriceAtSale()).orElse(BigDecimal.ZERO);

			BigDecimal costTotal = threshold.multiply(BigDecimal.valueOf(sale.getQuantity()));

			BigDecimal diff = saleTotal.subtract(costTotal);

			BigDecimal profit = BigDecimal.ZERO;
			BigDecimal loss = BigDecimal.ZERO;

			if (diff.compareTo(BigDecimal.ZERO) > 0) {
				profit = diff;
				totalProfit = totalProfit.add(profit);
			} else {
				loss = diff.abs();
				totalLoss = totalLoss.add(loss);
			}

			totalSales = totalSales.add(saleTotal);
			totalQty += sale.getQuantity();

			ProductSaleReportDTO dto = new ProductSaleReportDTO();
			dto.setProductName(product.getName());
			dto.setSku(variant.getProductSku());
			dto.setQuantity(sale.getQuantity());
			dto.setSaleTotal(saleTotal);
			dto.setCostTotal(costTotal);
			dto.setProfit(profit);
			dto.setLoss(loss);
			dto.setCustomerId(customerId);

			productReports.add(dto);
		}

		DetailedDailyReportDTO report = new DetailedDailyReportDTO();
		report.setDate(date);
		report.setCustomerId(customerId);
		report.setTotalQuantitySold(totalQty);
		report.setProductSales(productReports);
		report.setTotalSales(totalSales);
		report.setTotalProfit(totalProfit);
		report.setTotalLoss(totalLoss);

		return report;
	}

	/*
	 * ==========================================================
	 * ===================== ATTRIBUTE SUMMARY ==================
	 * ==========================================================
	 */

	private String getVariantAttributeSummary(ProductVariant variant, Long customerId) {

		List<VariantAttribute> attributes = variantAttributeRepo
				.findByVariant_VariantIdAndCustomer_Id(variant.getVariantId(), customerId);

		if (attributes == null || attributes.isEmpty())
			return "-";

		return attributes.stream().sorted(Comparator.comparing(a -> a.getAttributeValue().getAttribute().getId()))
				.map(a -> a.getAttributeValue().getValue()).collect(Collectors.joining(" / "));
	}

	/*
	 * ==========================================================
	 * ===================== YEARLY REPORT ======================
	 * ==========================================================
	 */

	public DetailedDailyReportDTO getYearlyReport(int year) {

		Long customerId = jwtUtils.getRequiredCustomerId();

		List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndSaleInvoice_Status(
				LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), customerId, "ACTIVE");

		DetailedDailyReportDTO report = calculateReport(sales, null, customerId);

		report.setYear(year);

		return report;
	}
}