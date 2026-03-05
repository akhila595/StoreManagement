package com.shopmanagement.service;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private AttributeRepository attributeRepository;
    @Autowired private ProductAttributeRepository productAttributeRepository;

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
       ===================== ENTITY → DTO =========================
       ============================================================ */

    private ProductDTO mapToDTO(Product product) {

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getProductId());
        dto.setName(product.getName());
        dto.setCode(product.getCode());

        dto.setImageUrl(
                product.getImageUrl() != null
                        ? product.getImageUrl()
                        : "/images/products/no-image.png"
        );

        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
            dto.setBrandName(product.getBrand().getBrand());
        }

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }

        return dto;
    }

    /* ============================================================
       ===================== CREATE ===============================
       ============================================================ */

    public Map<String, Object> createProduct(ProductDTO dto,
                                             MultipartFile imageFile) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 🔥 Auto generate product code if not provided
        String code = dto.getCode();

        if (code == null || code.isBlank()) {
            code = generateProductCode(dto.getName(), customerId);
        }

        // Prevent duplicate code per customer
        productRepository.findByCodeAndCustomer_Id(code, customerId)
                .ifPresent(p -> {
                    throw new RuntimeException("Product code already exists.");
                });

        Product product = new Product();
        product.setName(dto.getName());
        product.setCode(code);
        product.setCustomer(customer);

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));
            product.setBrand(brand);
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageUrl(saveImage(imageFile));
        }

        Product saved = productRepository.save(product);
        linkAttributesToProduct(saved, dto.getAttributeIds(), customer);
        return Map.of(
                "message", "Product created successfully",
                "data", mapToDTO(saved)
        );
    }

    /* ============================================================
       ===================== READ ALL =============================
       ============================================================ */

    public Map<String, Object> getAllProducts() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<ProductDTO> list = productRepository
                .findByCustomer_IdAndStatus(customerId,"ACTIVE")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return Map.of(
                "message", "Products fetched successfully",
                "data", list
        );
    }

    /* ============================================================
       ===================== READ BY ID ===========================
       ============================================================ */

    public Map<String, Object> getProductById(Long id) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Product product = productRepository
                .findByProductIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return Map.of(
                "message", "Product fetched successfully",
                "data", mapToDTO(product)
        );
    }

    /* ============================================================
       ===================== UPDATE ===============================
       ============================================================ */

    public Map<String, Object> updateProduct(Long id,
                                             ProductDTO dto,
                                             MultipartFile imageFile) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Product existing = productRepository
                .findByProductIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(dto.getName());

        if (dto.getCode() != null && !dto.getCode().equals(existing.getCode())) {

            productRepository.findByCodeAndCustomer_Id(dto.getCode(), customerId)
                    .ifPresent(p -> {
                        throw new RuntimeException("Product code already exists.");
                    });

            existing.setCode(dto.getCode());
        }

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));
            existing.setBrand(brand);
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setImageUrl(saveImage(imageFile));
        }

        Product updated = productRepository.save(existing);
        productAttributeRepository.deleteByProduct_ProductIdAndCustomer_Id(id, customerId);
        linkAttributesToProduct(existing, dto.getAttributeIds(), existing.getCustomer());
        return Map.of(
                "message", "Product updated successfully",
                "data", mapToDTO(updated)
        );
    }

    /* ============================================================
       ===================== DELETE ===============================
       ============================================================ */

    public Map<String, Object> deleteProduct(Long id) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Product product = productRepository
                .findByProductIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus("DELETED");
        productRepository.save(product);

        return Map.of("message", "Product deleted successfully");
    }

    /* ============================================================
       ===================== PRODUCT CODE GENERATOR ==============
       ============================================================ */

    private String generateProductCode(String name, Long customerId) {

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Product name required.");
        }

        String base = name.replaceAll("[^A-Za-z]", "")
                          .toUpperCase();

        if (base.length() > 6) {
            base = base.substring(0, 6);
        }

        String code = base;
        int counter = 1;

        while (productRepository.existsByCodeAndCustomer_Id(code, customerId)) {
            code = base + counter;
            counter++;
        }

        return code;
    }
    
    private void linkAttributesToProduct(
            Product product,
            List<Long> attributeIds,
            Customer customer) {

        if (attributeIds == null || attributeIds.isEmpty()) {
            return;
        }

        for (Long attributeId : attributeIds) {

            Attribute attribute = attributeRepository
                    .findByIdAndCustomer_Id(attributeId, customer.getId())
                    .orElseThrow(() -> new RuntimeException("Invalid attribute"));

            ProductAttribute pa = new ProductAttribute();
            pa.setProduct(product);
            pa.setAttribute(attribute);
            pa.setCustomer(customer);

            productAttributeRepository.save(pa);
        }
    }
    
}