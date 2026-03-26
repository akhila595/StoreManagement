package com.shopmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmanagement.dto.*;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

@Service
@Transactional
public class StockService {

    @Autowired private ProductRepository productRepo;
    @Autowired private ProductVariantRepository variantRepo;
    @Autowired private AttributeValueRepository attributeValueRepo;
    @Autowired private VariantAttributeRepository variantAttributeRepo;
    @Autowired private SupplierRepository supplierRepo;
    @Autowired private PurchaseRepository purchaseRepo;
    @Autowired private StockMovementRepository movementRepo;
    @Autowired private SaleInvoiceRepository saleInvoiceRepo;
    @Autowired private SaleItemRepository saleItemRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private JwtUtils jwtUtil;

    /* ============================================================
       ===================== STOCK IN =============================
       ============================================================ */
      //Discount = sellingPrice - finalPrice
    @Transactional
    public String stockIn(StockInRequestDTO dto) {

        Long customerId = jwtUtil.getRequiredCustomerId();

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepo
                .findByProductIdAndCustomer_Id(dto.getProductId(), customerId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<AttributeValue> attributeValues =
                attributeValueRepo.findAllById(dto.getAttributeValueIds());

        if (attributeValues.isEmpty()) {
            throw new RuntimeException("Invalid attribute values");
        }

        String sku = generateSku(product, dto.getAttributeValueIds());

        ProductVariant variant =
                variantRepo.findByProductSkuAndCustomer_Id(sku, customerId)
                        .orElse(null);

        if (variant != null) {

            variant.setStockQty(variant.getStockQty() + dto.getQuantity());
            variant.setCostPrice(dto.getCostPrice());
            variant.setSellingPrice(dto.getSellingPrice());

        } else {

            variant = new ProductVariant();
            variant.setProduct(product);
            variant.setProductSku(sku);
            variant.setCostPrice(dto.getCostPrice());
            variant.setSellingPrice(dto.getSellingPrice());
            variant.setStockQty(dto.getQuantity());
            variant.setCustomer(customer);

            variant = variantRepo.save(variant);

            for (AttributeValue value : attributeValues) {

                VariantAttribute va = new VariantAttribute();
                va.setVariant(variant);
                va.setAttributeValue(value);
                va.setCustomer(customer);

                variantAttributeRepo.save(va);
            }
        }

        variant = variantRepo.save(variant);

        Supplier supplier = supplierRepo
                .findBySupplierNameAndCustomer_Id(dto.getSupplierName(), customerId)
                .orElseGet(() -> {

                    Supplier s = new Supplier();
                    s.setSupplierName(dto.getSupplierName());
                    s.setCustomer(customer);

                    return supplierRepo.save(s);
                });

        BigDecimal thresholdPrice = dto.getCostPrice()
                .add(Optional.ofNullable(dto.getTaxPerUnit()).orElse(BigDecimal.ZERO))
                .add(Optional.ofNullable(dto.getTransportPerUnit()).orElse(BigDecimal.ZERO));

        Purchase purchase = new Purchase();
        purchase.setProductVariant(variant);
        purchase.setQuantity(dto.getQuantity());
        purchase.setThresholdPrice(thresholdPrice);
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setSupplier(supplier);
        purchase.setCustomer(customer);

        purchaseRepo.save(purchase);

        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setMovementType("IN");
        movement.setQuantity(dto.getQuantity());
        movement.setMovementDate(LocalDateTime.now());
        movement.setRemarks(dto.getRemarks());
        movement.setCustomer(customer);
        movement.setPurchase(purchase);

        movementRepo.save(movement);

        return "Stock added successfully for SKU: " + sku;
    }

    /* ============================================================
       ===================== STOCK OUT ============================
       ============================================================ */

    @Transactional
    public String stockOut(StockOutRequestDTO dto) {

        Long customerId = jwtUtil.getRequiredCustomerId();

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ProductVariant variant =
                variantRepo.findByProductSkuAndCustomer_Id(dto.getSku(), customerId)
                        .orElseThrow(() -> new RuntimeException("SKU not found"));

        if (variant.getStockQty() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        variant.setStockQty(variant.getStockQty() - dto.getQuantity());
        variantRepo.save(variant);

        Optional<Purchase> lastPurchaseOpt =
                purchaseRepo.findTopByProductVariantAndCustomer_IdOrderByPurchaseDateDesc(
                        variant, customerId);

        if (lastPurchaseOpt.isEmpty()) {
            throw new RuntimeException("No purchase record found for SKU");
        }

        Purchase lastPurchase = lastPurchaseOpt.get();

        SaleInvoice invoice = new SaleInvoice();
        invoice.setInvoiceId("INV-" + System.currentTimeMillis());
        invoice.setSaleDate(dto.getSaleDate().toLocalDate());
        invoice.setTotalAmount(
                dto.getFinalPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        invoice.setCustomer(customer);

        saleInvoiceRepo.save(invoice);

        SaleItem item = new SaleItem();
        item.setSaleInvoice(invoice);
        item.setProductVariant(variant);
        item.setQuantity(dto.getQuantity());
        item.setSellingPrice(variant.getSellingPrice());
        item.setFinalPrice(dto.getFinalPrice());
        item.setThresholdPriceAtSale(lastPurchase.getThresholdPrice());
        item.setCustomer(customer);

        saleItemRepo.save(item);

        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setMovementType("OUT");
        movement.setQuantity(dto.getQuantity());
        movement.setMovementDate(dto.getSaleDate());
        movement.setRemarks(dto.getRemarks());
        movement.setCustomer(customer);

        movementRepo.save(movement);

        return "Stock OUT successful. Invoice: " + invoice.getInvoiceId();
    }

    /* ============================================================
       ===================== SKU GENERATOR ========================
       ============================================================ */

    private String generateSku(Product product, List<Long> attributeValueIds) {

        Long customerId = jwtUtil.getRequiredCustomerId();

        if (product.getCode() == null || product.getCode().isBlank()) {
            throw new RuntimeException("Product code must be defined");
        }

        StringBuilder skuBuilder = new StringBuilder();
        skuBuilder.append(product.getCode().toUpperCase());

        if (attributeValueIds != null && !attributeValueIds.isEmpty()) {

            List<AttributeValue> values =
                    attributeValueRepo.findByIdInAndCustomer_Id(attributeValueIds, customerId);

            if (values.size() != attributeValueIds.size()) {
                throw new RuntimeException("Invalid attribute values selected");
            }

            values.stream()
                    .sorted(Comparator.comparing(v -> v.getAttribute().getId()))
                    .forEach(value -> {

                        if (value.getCode() == null || value.getCode().isBlank()) {
                            throw new RuntimeException("Attribute code missing");
                        }

                        skuBuilder.append("-")
                                  .append(value.getCode().toUpperCase());
                    });
        }

        return skuBuilder.toString();
    }

    /* ============================================================
       ===================== RECENT STOCK IN ======================
       ============================================================ */

    public List<RecentStockInDTO> getRecentStockIns() {

        Long customerId = jwtUtil.getRequiredCustomerId();

        List<Purchase> purchases =
                purchaseRepo.findTop10ByCustomer_IdAndStatusOrderByPurchaseDateDesc(
                        customerId, "ACTIVE");

        List<RecentStockInDTO> result = new ArrayList<>();

        for (Purchase purchase : purchases) {

            ProductVariant variant = purchase.getProductVariant();
            if (variant == null) continue;

            Product product = variant.getProduct();
            if (product == null || "DELETED".equals(product.getStatus()))
                continue;

            RecentStockInDTO dto = new RecentStockInDTO();
            dto.setProductName(product.getName());
            dto.setSku(variant.getProductSku());
            dto.setQuantityAdded(purchase.getQuantity());
            dto.setSupplierName(
                    purchase.getSupplier() != null
                            ? purchase.getSupplier().getSupplierName()
                            : "Unknown");
            dto.setStockInDate(purchase.getPurchaseDate());
            dto.setImageUrl(product.getImageUrl());
            dto.setCustomerId(customerId);

            result.add(dto);
        }

        return result;
    }

    /* ============================================================
       ===================== RECENT STOCK OUT =====================
       ============================================================ */

    public List<RecentStockOutDTO> getRecentStockOuts() {

        Long customerId = jwtUtil.getRequiredCustomerId();

        List<StockMovement> movements =
                movementRepo.findTop10ByMovementTypeAndCustomer_IdAndStatusOrderByMovementDateDesc(
                        "OUT", customerId, "ACTIVE");

        List<RecentStockOutDTO> result = new ArrayList<>();

        for (StockMovement movement : movements) {

            ProductVariant variant = movement.getProductVariant();
            if (variant == null) continue;

            Product product = variant.getProduct();
            if (product == null || "DELETED".equals(product.getStatus()))
                continue;

            RecentStockOutDTO dto = new RecentStockOutDTO();

            dto.setProductName(product.getName());
            dto.setSku(variant.getProductSku());
            dto.setQuantityRemoved(movement.getQuantity());
            dto.setReason(
                    movement.getRemarks() != null
                            ? movement.getRemarks()
                            : "Sale");
            dto.setStockOutDate(movement.getMovementDate());
            dto.setImageUrl(product.getImageUrl());
            dto.setCustomerId(customerId);

            result.add(dto);
        }

        return result;
    }
}