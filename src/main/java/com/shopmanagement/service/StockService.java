package com.shopmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopmanagement.dto.StockInRequestDTO;
import com.shopmanagement.dto.StockOutRequestDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StockService {

    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private ProductVariantRepository variantRepo;
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private PurchaseRepository purchaseRepo;
    @Autowired
    private StockMovementRepository movementRepo;
    @Autowired
    private SaleItemRepository saleItemRepo;
    @Autowired
    private  SaleInvoiceRepository saleInvoiceRepo;
    @Autowired
    private BrandRepository brandRepo;
    @Autowired
    private ColorRepository colorRepo;
    @Autowired
    private SizeRepository sizeRepo;
    @Autowired
    private ClothTypeRepository clothTypeRepo;
    /** STOCK IN **/
  
    @Transactional
    public String stockIn(StockInRequestDTO dto) {

        // 1. Fetch Category
        Category category = categoryRepo.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("❌ Category not found with ID: " + dto.getCategoryId()));

        // 2. Fetch Brand
        Brand brand = brandRepo.findById(dto.getBrandId())
            .orElseThrow(() -> new RuntimeException("❌ Brand not found with ID: " + dto.getBrandId()));

        // 3. Fetch Cloth Type
        ClothType clothType = clothTypeRepo.findById(dto.getClothTypeId())
            .orElseThrow(() -> new RuntimeException("❌ ClothType not found with ID: " + dto.getClothTypeId()));

        // 4. Create or fetch Product
        Product product = productRepo.findByDesignCode(dto.getDesignCode())
            .orElseGet(() -> {
                Product p = new Product();
                p.setDesignCode(dto.getDesignCode());
                p.setPattern(dto.getPattern());
                p.setBrand(brand);
                p.setClothType(clothType);
                p.setCategory(category);

                String generatedName = dto.getDesignCode() + " - " + brand.getBrand() + " - " + clothType.getClothType();
                p.setProductName(generatedName);

                return productRepo.save(p);
            });

        // 5. Fetch Color
        Color color = colorRepo.findById(dto.getColorId())
            .orElseThrow(() -> new RuntimeException("❌ Color not found with ID: " + dto.getColorId()));

        // 6. Fetch Size
        Size size = sizeRepo.findById(dto.getSizeId())
            .orElseThrow(() -> new RuntimeException("❌ Size not found with ID: " + dto.getSizeId()));

        // 7. Create or update ProductVariant
        ProductVariant variant = variantRepo.findByProductSku(dto.getSku()).orElse(null);
        if (variant != null) {
            variant.setStockQty(variant.getStockQty() + dto.getQuantity());
            variant.setSellingPrice(dto.getSellingPrice());
        } else {
            variant = new ProductVariant();
            variant.setProductSku(dto.getSku());
            variant.setColor(color);
            variant.setSize(size);
            variant.setSellingPrice(dto.getSellingPrice());
            variant.setStockQty(dto.getQuantity());
            variant.setProduct(product);
        }
        variant = variantRepo.save(variant);

        // 8. Supplier - still by name
        Supplier supplier = supplierRepo.findBySupplierName(dto.getSupplierName())
            .orElseGet(() -> {
                Supplier s = new Supplier();
                s.setSupplierName(dto.getSupplierName());
                return supplierRepo.save(s);
            });

        // 9. Calculate Threshold Price
        BigDecimal thresholdPrice = dto.getBasePrice()
            .add(dto.getTaxPerUnit() != null ? dto.getTaxPerUnit() : BigDecimal.ZERO)
            .add(dto.getTransportPerUnit() != null ? dto.getTransportPerUnit() : BigDecimal.ZERO);

        // 10. Save Purchase
        Purchase purchase = new Purchase();
        purchase.setProductVariant(variant);
        purchase.setQuantity(dto.getQuantity());
        purchase.setThresholdPrice(thresholdPrice);
        purchase.setPurchaseDate(LocalDate.parse(dto.getPurchaseDate()));
        purchase.setSupplier(supplier);
        purchaseRepo.save(purchase);

        // 11. Stock Movement
        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setMovementType("IN");
        movement.setQuantity(dto.getQuantity());
        movement.setMovementDate(LocalDateTime.now());
        movement.setRemarks(dto.getRemarks());
        movementRepo.save(movement);

        return "✅ Stock added successfully!";
    }


    /** STOCK OUT **/
    /** STOCK OUT **/
    public String stockOut(StockOutRequestDTO dto) {

        // 1. Find product variant
        ProductVariant variant = variantRepo.findByProductSku(dto.getSku())
                .orElseThrow(() -> new RuntimeException("Product variant not found for SKU: " + dto.getSku()));

        // 2. Check stock availability
        if (variant.getStockQty() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock for SKU: " + dto.getSku());
        }
        variant.setStockQty(variant.getStockQty() - dto.getQuantity());
        variantRepo.save(variant);

        // 3. Get latest purchase batch (for threshold price)
        Purchase lastPurchase = purchaseRepo.findTopByProductVariantOrderByPurchaseDateDesc(variant)
                .orElseThrow(() -> new RuntimeException("No purchase record found for SKU: " + dto.getSku()));

        // 4. Create SaleInvoice (minimal fields only)
        SaleInvoice invoice = new SaleInvoice();
        invoice.setInvoiceId("INV-" + System.currentTimeMillis()); // simple unique ID
        invoice.setSaleDate(dto.getSaleDate().toLocalDate());
        invoice.setTotalAmount(dto.getFinalPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        // skipping customerName, paymentMode, discountTotal for now
        invoice = saleInvoiceRepo.save(invoice);

        // 5. Save SaleItem
        SaleItem saleItem = new SaleItem();
        saleItem.setSaleInvoice(invoice);
        saleItem.setProductVariant(variant);
        saleItem.setQuantity(dto.getQuantity());
        saleItem.setSellingPrice(dto.getSellingPrice());
        saleItem.setFinalPrice(dto.getFinalPrice());
        saleItem.setThresholdPriceAtSale(lastPurchase.getThresholdPrice());
        saleItemRepo.save(saleItem);

        // 6. Stock Movement
        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setQuantity(dto.getQuantity());
        movement.setMovementType("OUT");
        movement.setMovementDate(dto.getSaleDate());
        movement.setRemarks(dto.getRemarks());
        movementRepo.save(movement);

        return "✅ Stock OUT successful for SKU: " + dto.getSku() + ", Invoice: " + invoice.getInvoiceId();
    }

}
