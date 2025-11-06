package com.shopmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopmanagement.dto.*;
import com.shopmanagement.model.Product;
import com.shopmanagement.model.ProductVariant;
import com.shopmanagement.model.Purchase;
import com.shopmanagement.model.SaleItem;
import com.shopmanagement.repository.ProductRepository;
import com.shopmanagement.repository.ProductVariantRepository;
import com.shopmanagement.repository.PurchaseRepository;
import com.shopmanagement.repository.SaleItemRepository;

@Service
public class ReportService {

    @Autowired
    private SaleItemRepository saleItemRepo;

    @Autowired
    private PurchaseRepository purchaseRepo;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    ProductVariantRepository productVariantRepository;

    /** DAILY REPORT **/
    public DetailedDailyReportDTO getDailyReport(LocalDate date) {
        return calculateReport(
                saleItemRepo.findBySaleInvoice_SaleDate(date),
                date
        );
    }

    /** MONTHLY REPORT **/
    public DetailedDailyReportDTO getMonthlyReport(YearMonth month) {
        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetween(
                month.atDay(1), month.atEndOfMonth()
        );

        DetailedDailyReportDTO report = calculateReport(sales, null);
        report.setMonth(month);
        return report;
    }

    /** CATEGORY-WISE PROFIT/LOSS **/
    public List<CategoryReportDTO> getCategoryWiseReport(LocalDate startDate, LocalDate endDate) {
        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetween(startDate, endDate);

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

    /** SUPPLIER PURCHASE HISTORY **/
    public List<PurchaseReportDTO> getSupplierPurchaseHistory(Long supplierId, LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases =
                purchaseRepo.findBySupplier_SupplierIdAndPurchaseDateBetween(supplierId, startDate, endDate);

        List<PurchaseReportDTO> reportList = new ArrayList<>();

        for (Purchase p : purchases) {
            PurchaseReportDTO dto = new PurchaseReportDTO();
            dto.setSupplierName(p.getSupplier().getSupplierName());
            dto.setPurchaseDate(p.getPurchaseDate());
            dto.setProductName(p.getProductVariant().getProduct().getProductName());
            dto.setQuantity(p.getQuantity());
            dto.setThresholdPrice(p.getThresholdPrice());

            reportList.add(dto);
        }

        
        return reportList;
    }

    /** TOP-SELLING PRODUCTS **/
    /** TOP-SELLING PRODUCTS **/
    public List<TopSellingProductDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetween(startDate, endDate);

        // Use Product as key so we can access both productName and imageUrl
        Map<Product, Integer> productQtyMap = new HashMap<>();
        for (SaleItem sale : sales) {
            Product product = sale.getProductVariant().getProduct();
            productQtyMap.put(product, productQtyMap.getOrDefault(product, 0) + sale.getQuantity());
        }

        // Convert map entries into DTO list with imageUrl
        return productQtyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // sort by quantity sold
                .limit(limit)
                .map(e -> new TopSellingProductDTO(
                        e.getKey().getProductName(),
                        e.getKey().getImageUrl(),  // ✅ fetch image url from product table
                        e.getValue()
                ))
                .collect(Collectors.toList());
    }

    /** COMMON REPORT CALCULATION **/
    private DetailedDailyReportDTO calculateReport(List<SaleItem> sales, LocalDate date) {
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

            productReports.add(productReport);
        }

        DetailedDailyReportDTO report = new DetailedDailyReportDTO();
        report.setDate(date);
        report.setTotalQuantitySold(totalQuantitySold);
        report.setProductSales(productReports);
        report.setTotalSales(totalSales);
        report.setTotalProfit(totalProfit);
        report.setTotalLoss(totalLoss);

        return report;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductDTO(
                        p.getProductId(), // id
                        p.getProductName(), // productName
                        p.getDesignCode(), // designCode
                        p.getPattern(), // pattern
                        p.getBrand() != null ? p.getBrand().getId() : null, // brandId
                        p.getBrand() != null ? p.getBrand().getBrand() : null, // brandName
                        p.getClothType() != null ? p.getClothType().getId() : null, // clothTypeId
                        p.getClothType() != null ? p.getClothType().getClothType() : null, // clothTypeName
                        p.getCategory() != null ? p.getCategory().getCategoryId() : null, // categoryId
                        p.getCategory() != null ? p.getCategory().getCategoryName() : null // categoryName
                ))
                .toList();
    }


    public List<LowStockProductDTO> getLowStockProducts() {
        int lowStockThreshold = 10; // Threshold for low stock.
        
        // Fetch product variants with low stock
        List<ProductVariant> lowStockVariants = productVariantRepository.findByStockQtyLessThan(lowStockThreshold);

        List<LowStockProductDTO> dtoList = new ArrayList<>();
        
        for (ProductVariant variant : lowStockVariants) {
            LowStockProductDTO dto = new LowStockProductDTO();
            dto.setProductName(variant.getProduct().getProductName());
            dto.setSku(variant.getProductSku()); // ✅ Set SKU
            dto.setStockQty(variant.getStockQty());
            dtoList.add(dto);
        }

        return dtoList;
    }

    public DetailedDailyReportDTO getWeeklyReport(LocalDate startDate, LocalDate endDate) {
        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetween(startDate, endDate);
        DetailedDailyReportDTO report = calculateReport(sales, null); // No specific "date"
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        return report;
    }
    public DetailedDailyReportDTO getYearlyReport(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<SaleItem> sales = saleItemRepo.findBySaleInvoice_SaleDateBetween(start, end);
        DetailedDailyReportDTO report = calculateReport(sales, null);
        report.setYear(year);
        return report;
    }

    public List<PurchaseReportDTO> getAllSuppliersPurchaseReport(LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases = purchaseRepo.findByPurchaseDateBetween(startDate, endDate);

        List<PurchaseReportDTO> reportList = new ArrayList<>();
        for (Purchase p : purchases) {
            PurchaseReportDTO dto = new PurchaseReportDTO();
            dto.setSupplierName(p.getSupplier().getSupplierName());
            dto.setPurchaseDate(p.getPurchaseDate());
            dto.setProductName(p.getProductVariant().getProduct().getProductName());
            dto.setQuantity(p.getQuantity());
            dto.setThresholdPrice(p.getThresholdPrice());

            reportList.add(dto);
        }

        return reportList;
    }

}
