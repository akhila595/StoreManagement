package com.shopmanagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shopmanagement.dto.*;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

@Service
@Transactional
public class StockService {

    @Autowired private CategoryRepository categoryRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private ProductVariantRepository variantRepo;
    @Autowired private SupplierRepository supplierRepo;
    @Autowired private PurchaseRepository purchaseRepo;
    @Autowired private StockMovementRepository movementRepo;
    @Autowired private SaleItemRepository saleItemRepo;
    @Autowired private SaleInvoiceRepository saleInvoiceRepo;
    @Autowired private BrandRepository brandRepo;
    @Autowired private ColorRepository colorRepo;
    @Autowired private SizeRepository sizeRepo;
    @Autowired private ClothTypeRepository clothTypeRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private JwtUtils jwtUtil;

    @Value("${app.upload.image-dir}")
    private String uploadImageDir;

    /** =========================
     *  SAVE IMAGE HELPER
     *  ========================= */
    private String saveImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;
        try {
            String ext = Optional.ofNullable(imageFile.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".")))
                    .orElse("");
            String uniqueFileName = UUID.randomUUID() + ext;
            Path uploadPath = Paths.get(uploadImageDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to store image file", e);
        }
    }

    /** =========================
     *  STOCK IN
     *  ========================= */
    public String stockIn(StockInRequestDTO dto, MultipartFile imageFile) {

        Long customerId = jwtUtil.getRequiredCustomerId();
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (imageFile != null && !imageFile.isEmpty()) {
            dto.setImageUrl(saveImage(imageFile));
        }

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepo.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        ClothType clothType = clothTypeRepo.findById(dto.getClothTypeId())
                .orElseThrow(() -> new RuntimeException("ClothType not found"));

        // ✅ find or create Product for this customer
        Product product = productRepo.findByDesignCodeAndCustomerId(dto.getDesignCode(), customerId)
                .orElseGet(() -> {
                    Product p = new Product();
                    p.setDesignCode(dto.getDesignCode());
                    p.setPattern(dto.getPattern());
                    p.setBrand(brand);
                    p.setClothType(clothType);
                    p.setCategory(category);
                    p.setProductName(dto.getDesignCode() + " - " + brand.getBrand());
                    p.setCustomer(customer);
                    if (dto.getImageUrl() != null) p.setImageUrl(dto.getImageUrl());
                    return productRepo.save(p);
                });

        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
            productRepo.save(product);
        }

        Color color = colorRepo.findById(dto.getColorId())
                .orElseThrow(() -> new RuntimeException("Color not found"));
        Size size = sizeRepo.findById(dto.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        // ✅ find or create variant
        ProductVariant variant = variantRepo.findByProductSkuAndCustomer_Id(dto.getSku(), customerId).orElse(null);
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
            variant.setCustomer(customer);
        }
        variant = variantRepo.save(variant);

        // ✅ find or create supplier
        Supplier supplier = supplierRepo.findBySupplierNameAndCustomer_Id(dto.getSupplierName(), customerId)
                .orElseGet(() -> {
                    Supplier s = new Supplier();
                    s.setSupplierName(dto.getSupplierName());
                    s.setCustomer(customer);
                    return supplierRepo.save(s);
                });

        BigDecimal thresholdPrice = dto.getBasePrice()
                .add(Optional.ofNullable(dto.getTaxPerUnit()).orElse(BigDecimal.ZERO))
                .add(Optional.ofNullable(dto.getTransportPerUnit()).orElse(BigDecimal.ZERO));

        Purchase purchase = new Purchase();
        purchase.setProductVariant(variant);
        purchase.setQuantity(dto.getQuantity());
        purchase.setThresholdPrice(thresholdPrice);
        purchase.setPurchaseDate(LocalDate.parse(dto.getPurchaseDate()));
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
        movementRepo.save(movement);

        return "✅ Stock added successfully for customer " + customerId;
    }

    /** =========================
     *  STOCK OUT
     *  ========================= */
    public String stockOut(StockOutRequestDTO dto) {
        Long customerId = jwtUtil.getRequiredCustomerId();
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Optional<ProductVariant> variantOpt = variantRepo.findByProductSkuAndCustomer_Id(dto.getSku(), customerId);
        if (variantOpt.isEmpty()) return "❌ SKU not found for your account.";

        ProductVariant variant = variantOpt.get();
        if (variant.getStockQty() < dto.getQuantity()) return "❌ Insufficient stock.";

        variant.setStockQty(variant.getStockQty() - dto.getQuantity());
        variantRepo.save(variant);

        Optional<Purchase> lastPurchaseOpt =
                purchaseRepo.findTopByProductVariantAndCustomer_IdOrderByPurchaseDateDesc(variant, customerId);
        if (lastPurchaseOpt.isEmpty()) return "❌ No purchase found for SKU.";

        Purchase lastPurchase = lastPurchaseOpt.get();

        SaleInvoice invoice = new SaleInvoice();
        invoice.setInvoiceId("INV-" + System.currentTimeMillis());
        invoice.setSaleDate(dto.getSaleDate().toLocalDate());
        invoice.setTotalAmount(dto.getFinalPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        invoice.setCustomer(customer);
        saleInvoiceRepo.save(invoice);

        SaleItem saleItem = new SaleItem();
        saleItem.setSaleInvoice(invoice);
        saleItem.setProductVariant(variant);
        saleItem.setQuantity(dto.getQuantity());
        saleItem.setSellingPrice(dto.getSellingPrice());
        saleItem.setFinalPrice(dto.getFinalPrice());
        saleItem.setThresholdPriceAtSale(lastPurchase.getThresholdPrice());
        saleItem.setCustomer(customer);
        saleItemRepo.save(saleItem);

        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setQuantity(dto.getQuantity());
        movement.setMovementType("OUT");
        movement.setMovementDate(dto.getSaleDate());
        movement.setRemarks(dto.getRemarks());
        movement.setCustomer(customer);
        movementRepo.save(movement);

        return "✅ Stock OUT successful for SKU: " + dto.getSku() + ", Invoice: " + invoice.getInvoiceId();
    }

    /** =========================
     *  RECENT STOCK IN/OUT
     *  ========================= */
    public List<RecentStockInDTO> getRecentStockIns() {
        Long customerId = jwtUtil.getRequiredCustomerId();

        List<Purchase> recentPurchases = purchaseRepo.findTop10ByCustomer_IdOrderByPurchaseDateDesc(customerId);

        return recentPurchases.stream().map(p -> {
            ProductVariant v = p.getProductVariant();
            Product prod = v != null ? v.getProduct() : null;
            return new RecentStockInDTO(
                    prod != null ? prod.getProductName() : "Unknown",
                    v != null ? v.getProductSku() : "N/A",
                    Optional.ofNullable(p.getQuantity()).orElse(0),
                    p.getSupplier() != null ? p.getSupplier().getSupplierName() : "Unknown",
                    p.getPurchaseDate(),
                    prod != null ? prod.getImageUrl() : null
            );
        }).collect(Collectors.toList());
    }

    public List<RecentStockOutDTO> getRecentStockOuts() {
        Long customerId = jwtUtil.getRequiredCustomerId();

        List<StockMovement> outs =
                movementRepo.findTop10ByMovementTypeAndCustomer_IdOrderByMovementDateDesc("OUT", customerId);

        return outs.stream().map(s -> {
            ProductVariant v = s.getProductVariant();
            Product p = v != null ? v.getProduct() : null;
            return new RecentStockOutDTO(
                    p != null ? p.getProductName() : "Unknown",
                    v != null ? v.getProductSku() : "N/A",
                    Optional.ofNullable(s.getQuantity()).orElse(0),
                    s.getRemarks() != null ? s.getRemarks() : "N/A",
                    s.getMovementDate(),
                    p != null ? p.getImageUrl() : null
            );
        }).collect(Collectors.toList());
    }
}
