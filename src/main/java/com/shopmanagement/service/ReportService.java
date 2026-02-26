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

    @Autowired private SaleItemRepository saleItemRepo;
    @Autowired private PurchaseRepository purchaseRepo;
    @Autowired private ProductVariantRepository variantRepo;
    @Autowired private VariantAttributeRepository variantAttributeRepo;
    @Autowired private JwtUtils jwtUtils;

    /* ==========================================================
       ===================== DAILY REPORT =======================
       ========================================================== */

    public DetailedDailyReportDTO getDailyReport(LocalDate date) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales =
                saleItemRepo.findBySaleInvoice_SaleDateAndCustomer_IdAndStatus(
                        date, customerId, "ACTIVE");

        return calculateReport(sales, date, customerId);
    }

    /* ==========================================================
       ===================== MONTHLY REPORT =====================
       ========================================================== */

    public DetailedDailyReportDTO getMonthlyReport(YearMonth month) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales =
                saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndStatus(
                        month.atDay(1),
                        month.atEndOfMonth(),
                        customerId,
                        "ACTIVE");

        DetailedDailyReportDTO report = calculateReport(sales, null, customerId);
        report.setMonth(month);

        return report;
    }

    /* ==========================================================
       ===================== TOP SELLING PRODUCTS ===============
       ========================================================== */

    public List<TopSellingProductDTO> getTopSellingProducts(
            LocalDate startDate,
            LocalDate endDate,
            int limit) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales =
                saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndStatus(
                        startDate,
                        endDate,
                        customerId,
                        "ACTIVE");

        Map<ProductVariant, Integer> variantQtyMap = new HashMap<>();

        for (SaleItem sale : sales) {

            if (sale.getProductVariant() == null) continue;

            ProductVariant variant = sale.getProductVariant();

            variantQtyMap.put(
                    variant,
                    variantQtyMap.getOrDefault(variant, 0) + sale.getQuantity()
            );
        }

        return variantQtyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(entry -> {

                    ProductVariant variant = entry.getKey();
                    Product product = variant.getProduct();

                    String attributeSummary =
                            getVariantAttributeSummary(variant, customerId);

                    return new TopSellingProductDTO(
                            product != null ? product.getName() : "Unknown",
                            product != null && product.getBrand() != null
                                    ? product.getBrand().getBrand()
                                    : "-",
                            variant.getProductSku(),
                            attributeSummary,
                            entry.getValue(),
                            product != null ? product.getImageUrl() : null,
                            customerId
                    );
                })
                .collect(Collectors.toList());
    }

    /* ==========================================================
       ===================== LOW STOCK PRODUCTS =================
       ========================================================== */

    public List<LowStockProductDTO> getLowStockProducts(int threshold) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<ProductVariant> lowStockVariants =
                variantRepo.findByStockQtyLessThanAndCustomer_Id(
                        threshold, customerId);

        List<LowStockProductDTO> dtoList = new ArrayList<>();

        for (ProductVariant variant : lowStockVariants) {

            if (variant.getProduct() == null) continue;

            String attributeSummary =
                    getVariantAttributeSummary(variant, customerId);

            LowStockProductDTO dto = new LowStockProductDTO();
            dto.setProductName(variant.getProduct().getName());
            dto.setSku(variant.getProductSku());
            dto.setStockQty(variant.getStockQty());
            dto.setAttributes(attributeSummary);
            dto.setCustomerId(customerId);

            dtoList.add(dto);
        }

        return dtoList;
    }

    /* ==========================================================
       ===================== CATEGORY REPORT ====================
       ========================================================== */

    public List<CategoryReportDTO> getCategoryWiseReport(
            LocalDate startDate,
            LocalDate endDate) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<SaleItem> sales =
                saleItemRepo.findBySaleInvoice_SaleDateBetweenAndCustomer_IdAndStatus(
                        startDate,
                        endDate,
                        customerId,
                        "ACTIVE");

        Map<String, CategoryReportDTO> categoryMap = new HashMap<>();

        for (SaleItem sale : sales) {

            if (sale.getProductVariant() == null ||
                sale.getProductVariant().getProduct() == null ||
                sale.getProductVariant().getProduct().getCategory() == null)
                continue;

            String categoryName =
                    sale.getProductVariant()
                        .getProduct()
                        .getCategory()
                        .getCategoryName();

            CategoryReportDTO categoryReport =
                    categoryMap.getOrDefault(
                            categoryName,
                            new CategoryReportDTO(categoryName)
                    );

            BigDecimal saleTotal =
                    sale.getFinalPrice()
                        .multiply(BigDecimal.valueOf(sale.getQuantity()));

            BigDecimal threshold =
                    Optional.ofNullable(sale.getThresholdPriceAtSale())
                            .orElse(BigDecimal.ZERO);

            BigDecimal costTotal =
                    threshold.multiply(BigDecimal.valueOf(sale.getQuantity()));

            categoryReport.addSale(saleTotal, costTotal);

            categoryMap.put(categoryName, categoryReport);
        }

        return new ArrayList<>(categoryMap.values());
    }

    /* ==========================================================
       ===================== PURCHASE REPORT ====================
       ========================================================== */

    public List<PurchaseReportDTO> getPurchaseReport(
            LocalDate startDate,
            LocalDate endDate) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Purchase> purchases =
                purchaseRepo.findByPurchaseDateBetweenAndCustomer_IdAndStatus(
                        startDate,
                        endDate,
                        customerId,
                        "ACTIVE");

        List<PurchaseReportDTO> reportList = new ArrayList<>();

        for (Purchase p : purchases) {

            if (p.getProductVariant() == null) continue;

            PurchaseReportDTO dto = new PurchaseReportDTO();

            dto.setSupplierName(
                    p.getSupplier() != null
                            ? p.getSupplier().getSupplierName()
                            : "Unknown");

            dto.setProductName(
                    p.getProductVariant().getProduct() != null
                            ? p.getProductVariant().getProduct().getName()
                            : "Unknown");

            dto.setSku(p.getProductVariant().getProductSku());
            dto.setQuantity(p.getQuantity());
            dto.setThresholdPrice(p.getThresholdPrice());
            dto.setPurchaseDate(p.getPurchaseDate());
            dto.setCustomerId(customerId);

            reportList.add(dto);
        }

        return reportList;
    }

    /* ==========================================================
       ===================== COMMON REPORT LOGIC =================
       ========================================================== */

    private DetailedDailyReportDTO calculateReport(
            List<SaleItem> sales,
            LocalDate date,
            Long customerId) {

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalLoss = BigDecimal.ZERO;
        int totalQuantitySold = 0;

        List<ProductSaleReportDTO> productReports = new ArrayList<>();

        for (SaleItem sale : sales) {

            if (sale.getProductVariant() == null) continue;

            BigDecimal saleTotal =
                    sale.getFinalPrice()
                        .multiply(BigDecimal.valueOf(sale.getQuantity()));

            BigDecimal threshold =
                    Optional.ofNullable(sale.getThresholdPriceAtSale())
                            .orElse(BigDecimal.ZERO);

            BigDecimal costTotal =
                    threshold.multiply(BigDecimal.valueOf(sale.getQuantity()));

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

            ProductVariant variant = sale.getProductVariant();

            String attributeSummary =
                    getVariantAttributeSummary(variant, customerId);

            ProductSaleReportDTO productReport =
                    new ProductSaleReportDTO();

            productReport.setProductName(
                    variant.getProduct() != null
                            ? variant.getProduct().getName()
                            : "Unknown");

            productReport.setSku(variant.getProductSku());
            productReport.setAttributes(attributeSummary);
            productReport.setQuantity(sale.getQuantity());
            productReport.setSaleTotal(saleTotal);
            productReport.setCostTotal(costTotal);
            productReport.setProfit(profit);
            productReport.setLoss(loss);
            productReport.setCustomerId(customerId);

            productReports.add(productReport);
        }

        DetailedDailyReportDTO report =
                new DetailedDailyReportDTO();

        report.setDate(date);
        report.setCustomerId(customerId);
        report.setTotalQuantitySold(totalQuantitySold);
        report.setProductSales(productReports);
        report.setTotalSales(totalSales);
        report.setTotalProfit(totalProfit);
        report.setTotalLoss(totalLoss);

        return report;
    }

    /* ==========================================================
       ===================== ATTRIBUTE SUMMARY ==================
       ========================================================== */

    private String getVariantAttributeSummary(
            ProductVariant variant,
            Long customerId) {

        List<VariantAttribute> attributes =
                variantAttributeRepo
                        .findByVariant_VariantIdAndCustomer_Id(
                                variant.getVariantId(),
                                customerId);

        if (attributes == null || attributes.isEmpty())
            return "-";

        return attributes.stream()
                .sorted(Comparator.comparing(
                        a -> a.getAttributeValue()
                              .getAttribute()
                              .getId()))
                .map(a -> a.getAttributeValue().getValue())
                .collect(Collectors.joining(" / "));
    }
}