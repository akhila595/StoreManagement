package com.shopmanagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired private ProductRepository productRepo;
    @Autowired private ProductVariantRepository variantRepo;
    @Autowired private ProductAttributeRepository productAttributeRepo;
    @Autowired private AttributeValueRepository attributeValueRepo;
    @Autowired private VariantAttributeRepository variantAttributeRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private BrandRepository brandRepo;
    @Autowired private SupplierRepository supplierRepo;
    @Autowired private PurchaseRepository purchaseRepo;
    @Autowired private StockMovementRepository movementRepo;
    @Autowired private SaleInvoiceRepository saleInvoiceRepo;
    @Autowired private SaleItemRepository saleItemRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private JwtUtils jwtUtil;

    @Value("${app.upload.image-dir}")
    private String uploadImageDir;

    /* ============================================================
       ===================== IMAGE SAVE ===========================
       ============================================================ */

    private String saveImage(MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()) return null;

        try {
            String ext = Optional.ofNullable(imageFile.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".")))
                    .orElse("");

            String fileName = UUID.randomUUID() + ext;

            Path productImageDir = Paths.get(uploadImageDir, "products");

            if (!Files.exists(productImageDir)) {
                Files.createDirectories(productImageDir);
            }

            Path filePath = productImageDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/images/products/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store product image", e);
        }
    }

    /* ============================================================
       ===================== STOCK IN =============================
       ============================================================ */

    public String stockIn(StockInRequestDTO dto, MultipartFile imageFile) {

        Long customerId = jwtUtil.getRequiredCustomerId();
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product;

        if (dto.getProductId() != null) {

            product = productRepo.findByProductIdAndCustomer_Id(dto.getProductId(), customerId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if ("DELETED".equals(product.getStatus())) {
                throw new RuntimeException("Cannot add stock to a deleted product.");
            }

        } else {

            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Brand brand = brandRepo.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));

            product = new Product();
            product.setName(dto.getProductName());
            product.setCode(generateProductCode(dto.getProductName(), customerId));
            product.setCategory(category);
            product.setBrand(brand);
            product.setCustomer(customer);
            product.setStatus("ACTIVE");

            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImageUrl(saveImage(imageFile));
            }

            product = productRepo.save(product);
        }

        // ATTRIBUTE VALIDATION
        List<ProductAttribute> productAttributes =
                productAttributeRepo.findByProduct_productIdAndCustomer_Id(product.getProductId(), customerId);

        List<Long> selectedValueIds =
                dto.getAttributeValueIds() != null ? dto.getAttributeValueIds() : new ArrayList<>();

        if (!productAttributes.isEmpty()) {

            if (selectedValueIds.size() != productAttributes.size()) {
                throw new RuntimeException("All attributes must be selected.");
            }

            Set<Long> attributeIds = new HashSet<>();

            for (Long valueId : selectedValueIds) {

                AttributeValue value = attributeValueRepo
                        .findByIdAndCustomer_Id(valueId, customerId)
                        .orElseThrow(() -> new RuntimeException("Invalid attribute value"));

                Long attributeId = value.getAttribute().getId();

                boolean belongsToProduct = productAttributes.stream()
                        .anyMatch(pa -> pa.getAttribute().getId().equals(attributeId));

                if (!belongsToProduct) {
                    throw new RuntimeException("Attribute value not valid for this product.");
                }

                if (!attributeIds.add(attributeId)) {
                    throw new RuntimeException("Duplicate attribute selection.");
                }
            }
        }

        // GENERATE SKU
        String sku = generateSku(product, selectedValueIds);

        Optional<ProductVariant> existingVariant =
                variantRepo.findByProductSkuAndCustomer_Id(sku, customerId);

        ProductVariant variant;

        if (existingVariant.isPresent()) {

            variant = existingVariant.get();

        } else {

            variant = new ProductVariant();
            variant.setProduct(product);
            variant.setCustomer(customer);
            variant.setProductSku(sku);
            variant.setStockQty(0);
            variant.setCostPrice(dto.getCostPrice());
            variant.setSellingPrice(dto.getSellingPrice());

            variant = variantRepo.save(variant);

            // ✅ FIX 2: Use Set to prevent duplicates
            Set<Long> uniqueValueIds = new HashSet<>(selectedValueIds);

            for (Long valueId : uniqueValueIds) {

                AttributeValue value = attributeValueRepo
                        .findByIdAndCustomer_Id(valueId, customerId)
                        .orElseThrow(() -> new RuntimeException("Invalid attribute value"));

                VariantAttribute va = new VariantAttribute();
                va.setVariant(variant);
                va.setAttributeValue(value);
                va.setCustomer(customer);

                variantAttributeRepo.save(va);
            }
        }

        // UPDATE STOCK
        variant.setStockQty(variant.getStockQty() + dto.getQuantity());
        variant.setCostPrice(dto.getCostPrice());
        variant.setSellingPrice(dto.getSellingPrice());
        variantRepo.save(variant);

        // SUPPLIER
        Supplier supplier = supplierRepo
                .findBySupplierNameAndCustomer_Id(dto.getSupplierName(), customerId)
                .orElseGet(() -> {
                    Supplier s = new Supplier();
                    s.setSupplierName(dto.getSupplierName());
                    s.setCustomer(customer);
                    return supplierRepo.save(s);
                });

        // PURCHASE
        Purchase purchase = new Purchase();
        purchase.setProductVariant(variant);
        purchase.setQuantity(dto.getQuantity());
        purchase.setThresholdPrice(dto.getCostPrice());
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setSupplier(supplier);
        purchase.setCustomer(customer);
        purchaseRepo.save(purchase);

        // STOCK MOVEMENT
        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setMovementType("IN");
        movement.setQuantity(dto.getQuantity());
        movement.setPurchase(purchase);
        movement.setMovementDate(LocalDateTime.now());
        movement.setRemarks(dto.getRemarks());
        movement.setCustomer(customer);
        movementRepo.save(movement);

        return "Stock added successfully.";
    }

    /* ============================================================
       ===================== STOCK OUT ============================
       ============================================================ */

    public String stockOut(StockOutRequestDTO dto) {

        Long customerId = jwtUtil.getRequiredCustomerId();
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ProductVariant variant =
                variantRepo.findByProductSkuAndCustomer_Id(dto.getSku(), customerId)
                        .orElseThrow(() -> new RuntimeException("SKU not found."));

        if (variant.getStockQty() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock.");
        }

        variant.setStockQty(variant.getStockQty() - dto.getQuantity());
        variantRepo.save(variant);

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

        // ✅ FIX 3: Save threshold price at sale
        item.setThresholdPriceAtSale(variant.getCostPrice());

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
            throw new RuntimeException("Product code must be defined.");
        }

        StringBuilder skuBuilder = new StringBuilder();
        skuBuilder.append(product.getCode().toUpperCase());

        if (attributeValueIds != null && !attributeValueIds.isEmpty()) {

            List<AttributeValue> values =
                    attributeValueRepo.findByIdInAndCustomer_Id(attributeValueIds, customerId);

            // ✅ FIX 1: Validate count
            if (values.size() != attributeValueIds.size()) {
                throw new RuntimeException("Invalid attribute values selected.");
            }

            values.stream()
                    .sorted(Comparator.comparing(v -> v.getAttribute().getId()))
                    .forEach(value -> {

                        if (value.getCode() == null || value.getCode().isBlank()) {
                            throw new RuntimeException("Attribute code missing.");
                        }

                        skuBuilder.append("-")
                                  .append(value.getCode().toUpperCase());
                    });
        }

        return skuBuilder.toString();
    }

    private String generateProductCode(String name, Long customerId) {

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Product name required.");
        }

        String base = name.replaceAll("[^A-Za-z]", "").toUpperCase();

        if (base.length() > 6) {
            base = base.substring(0, 6);
        }

        String code = base;
        int counter = 1;

        while (productRepo.existsByCodeAndCustomer_Id(code, customerId)) {
            code = base + counter;
            counter++;
        }

        return code;
    }
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
                            : "Unknown"
            );
            dto.setStockInDate(purchase.getPurchaseDate());
            dto.setImageUrl(product.getImageUrl());
            dto.setCustomerId(customerId);

            result.add(dto);
        }

        return result;
    }
    
    public List<RecentStockOutDTO> getRecentStockOuts() {

        Long customerId = jwtUtil.getRequiredCustomerId();

        List<StockMovement> movements =
                movementRepo
                    .findTop10ByMovementTypeAndCustomer_IdAndStatusOrderByMovementDateDesc(
                            "OUT",
                            customerId,
                            "ACTIVE"
                    );

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
                            : "Sale"
            );
            dto.setStockOutDate(movement.getMovementDate());
            dto.setImageUrl(product.getImageUrl());
            dto.setCustomerId(customerId);

            result.add(dto);
        }

        return result;
    }
}