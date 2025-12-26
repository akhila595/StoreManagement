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
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // ==========================================================
    // DAILY REPORT
    // ==========================================================
    public DetailedDailyReportDTO getDailyReport(LocalDate date) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateAndCustomer_Id(date, customerId);

        return calculateReport(sales, date, customerId);
    }

    // ==========================================================
    // MONTHLY REPORT
    // ==========================================================
    public DetailedDailyReportDTO getMonthlyReport(YearMonth month) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_Id(
                month.atDay(1), month.atEndOfMonth(), customerId);

        DetailedDailyReportDTO report = calculateReport(sales, null, customerId);
        report.setMonth(month);
        return report;
    }

    // ==========================================================
    // WEEKLY REPORT
    // ==========================================================
    public DetailedDailyReportDTO getWeeklyReport(LocalDate startDate, LocalDate endDate) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_Id(startDate, endDate, customerId);

        DetailedDailyReportDTO report = calculateReport(sales, null, customerId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        return report;
    }

    // ==========================================================
    // YEARLY REPORT
    // ==========================================================
    public DetailedDailyReportDTO getYearlyReport(int year) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_Id(start, end, customerId);

        DetailedDailyReportDTO report = calculateReport(sales, null, customerId);
        report.setYear(year);
        return report;
    }

    // ==========================================================
    // CATEGORY-WISE PROFIT/LOSS
    // ==========================================================
    public List<CategoryReportDTO> getCategoryWiseReport(LocalDate startDate, LocalDate endDate) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_Id(startDate, endDate, customerId);

        Map<String, CategoryReportDTO> categoryMap = new HashMap<>();

        for (SaleItem sale : sales) {
            String categoryName = sale.getProductVariant().getProduct().getCategory().getCategoryName();
            CategoryReportDTO categoryReport = categoryMap
                    .getOrDefault(categoryName, new CategoryReportDTO(categoryName));

            BigDecimal saleTotal = sale.getFinalPrice().multiply(BigDecimal.valueOf(sale.getQuantity()));
            BigDecimal costTotal = sale.getThresholdPriceAtSale().multiply(BigDecimal.valueOf(sale.getQuantity()));

            categoryReport.addSale(saleTotal, costTotal);
            categoryMap.put(categoryName, categoryReport);
        }

        return new ArrayList<>(categoryMap.values());
    }

    // ==========================================================
    // SUPPLIER PURCHASE HISTORY
    // ==========================================================
    public List<PurchaseReportDTO> getSupplierPurchaseHistory(Long supplierId, LocalDate startDate, LocalDate endDate) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Purchase> purchases = purchaseRepo.findBySupplier_SupplierIdAndPurchaseDateBetweenAndCustomer_Id(
                supplierId, startDate, endDate, customerId);

        List<PurchaseReportDTO> reportList = new ArrayList<>();

        for (Purchase p : purchases) {
            PurchaseReportDTO dto = new PurchaseReportDTO();
            dto.setSupplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : "Unknown");
            dto.setPurchaseDate(p.getPurchaseDate());
            dto.setProductName(p.getProductVariant() != null
                    ? p.getProductVariant().getProduct().getProductName()
                    : "Unknown");
            dto.setQuantity(p.getQuantity());
            dto.setThresholdPrice(p.getThresholdPrice());
            dto.setCustomerId(customerId);
            reportList.add(dto);
        }

        return reportList;
    }

    // ==========================================================
    // TOP-SELLING PRODUCTS
    // ==========================================================
    public List<TopSellingProductDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_Id(startDate, endDate, customerId);

        Map<ProductVariant, Integer> variantQtyMap = new HashMap<>();
        for (SaleItem sale : sales) {
            ProductVariant variant = sale.getProductVariant();
            variantQtyMap.put(variant, variantQtyMap.getOrDefault(variant, 0) + sale.getQuantity());
        }

        return variantQtyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(e -> {
                    ProductVariant v = e.getKey();
                    Product p = v.getProduct();

                    return new TopSellingProductDTO(
                            p.getProductName(),
                            p.getBrand() != null ? p.getBrand().getBrand() : "-",
                            p.getPattern() != null ? p.getPattern() : "-",
                            p.getClothType() != null ? p.getClothType().getClothType() : "-",
                            v.getColor() != null ? v.getColor().getColor() : "-",
                            v.getSize() != null ? v.getSize().getSize() : "-",
                            e.getValue(),
                            p.getImageUrl(),
                            customerId
                    );
                })
                .collect(Collectors.toList());
    }

    // ==========================================================
    // LOW STOCK PRODUCTS
    // ==========================================================
    public List<LowStockProductDTO> getLowStockProducts() {
        Long customerId = jwtUtils.getRequiredCustomerId();

        int lowStockThreshold = 10;
        List<ProductVariant> lowStockVariants =
                productVariantRepository.findByStockQtyLessThanAndCustomer_Id(lowStockThreshold, customerId);

        List<LowStockProductDTO> dtoList = new ArrayList<>();
        for (ProductVariant variant : lowStockVariants) {
            LowStockProductDTO dto = new LowStockProductDTO();
            dto.setProductName(variant.getProduct().getProductName());
            dto.setSku(variant.getProductSku());
            dto.setStockQty(variant.getStockQty());
            dto.setCustomerId(customerId);
            dtoList.add(dto);
        }

        return dtoList;
    }

    // ==========================================================
    // COMMON REPORT CALCULATION
    // ==========================================================
    private DetailedDailyReportDTO calculateReport(List<SaleItem> sales, LocalDate date, Long customerId) {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalLoss = BigDecimal.ZERO;
        int totalQuantitySold = 0;

        List<ProductSaleReportDTO> productReports = new ArrayList<>();

        for (SaleItem sale : sales) {
            BigDecimal saleTotal = sale.getFinalPrice().multiply(BigDecimal.valueOf(sale.getQuantity()));
            BigDecimal costTotal = sale.getThresholdPriceAtSale().multiply(BigDecimal.valueOf(sale.getQuantity()));
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
            totalQuantitySold += sale.getQuantity();

            ProductSaleReportDTO productReport = new ProductSaleReportDTO();
            productReport.setProductName(sale.getProductVariant().getProduct().getProductName());
            productReport.setSku(sale.getProductVariant().getProductSku());
            productReport.setQuantity(sale.getQuantity());
            productReport.setSaleTotal(saleTotal);
            productReport.setCostTotal(costTotal);
            productReport.setProfit(profit);
            productReport.setLoss(loss);
            productReport.setCustomerId(customerId);

            productReports.add(productReport);
        }

        DetailedDailyReportDTO report = new DetailedDailyReportDTO();
        report.setDate(date);
        report.setCustomerId(customerId);
        report.setTotalQuantitySold(totalQuantitySold);
        report.setProductSales(productReports);
        report.setTotalSales(totalSales);
        report.setTotalProfit(totalProfit);
        report.setTotalLoss(totalLoss);

        return report;
    }

    // ==========================================================
    // SUPPLIER PURCHASE SUMMARY (All Suppliers)
    // ==========================================================
    public List<PurchaseReportDTO> getAllSuppliersPurchaseReport(LocalDate startDate, LocalDate endDate) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Purchase> purchases = purchaseRepo.findByPurchaseDateBetweenAndCustomer_Id(startDate, endDate, customerId);

        List<PurchaseReportDTO> reportList = new ArrayList<>();

        for (Purchase p : purchases) {
            PurchaseReportDTO dto = new PurchaseReportDTO();
            dto.setSupplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : "Unknown");
            dto.setProductName(p.getProductVariant() != null
                    ? p.getProductVariant().getProduct().getProductName()
                    : "Unknown");
            dto.setQuantity(p.getQuantity());
            dto.setThresholdPrice(p.getThresholdPrice());
            dto.setPurchaseDate(p.getPurchaseDate());
            dto.setCustomerId(customerId);
            reportList.add(dto);
        }

        return reportList;
    }
}
