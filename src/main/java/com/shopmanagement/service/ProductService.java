package com.shopmanagement.service;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ClothTypeRepository clothTypeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JwtUtils jwtUtils;

    // =========================
    // Helper: Entity → DTO
    // =========================
    private ProductDTO mapToDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getProductId());
        dto.setProductName(p.getProductName());
        dto.setDesignCode(p.getDesignCode());
        dto.setPattern(p.getPattern());
        dto.setImageUrl(
        	    (p.getImageUrl() != null && !p.getImageUrl().isEmpty())
        	        ? p.getImageUrl()
        	        : "/images/products/no-image.png"
        	);

        if (p.getBrand() != null) {
            dto.setBrandId(p.getBrand().getId());
            dto.setBrandName(p.getBrand().getBrand());
        }

        if (p.getClothType() != null) {
            dto.setClothTypeId(p.getClothType().getId());
            dto.setClothTypeName(p.getClothType().getClothType());
        }

        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getCategoryId());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }

        if (p.getCustomer() != null) {
            dto.setCustomerId(p.getCustomer().getId());
        }

        return dto;
    }

    // =========================
    // Helper: DTO → Entity
    // =========================
    private Product mapToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setProductId(dto.getId());
        product.setProductName(dto.getProductName());
        product.setDesignCode(dto.getDesignCode());
        product.setPattern(dto.getPattern());
        product.setImageUrl(dto.getImageUrl());

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with ID: " + dto.getBrandId()));
            product.setBrand(brand);
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));
            product.setCategory(category);
        }

        if (dto.getClothTypeId() != null) {
            ClothType clothType = clothTypeRepository.findById(dto.getClothTypeId())
                    .orElseThrow(() -> new RuntimeException("Cloth Type not found with ID: " + dto.getClothTypeId()));
            product.setClothType(clothType);
        }

        // ✅ Link customer from JWT token
        Long customerId = jwtUtils.getRequiredCustomerId();
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
            product.setCustomer(customer);
        }

        return product;
    }

    // =========================
    // CREATE
    // =========================
    public Map<String, Object> createProduct(ProductDTO dto) {
        Product product = mapToEntity(dto);
        Product saved = productRepository.save(product);

        return Map.of(
                "message", "Product created successfully",
                "data", mapToDTO(saved)
        );
    }

    // =========================
    // READ ALL (Customer Scoped)
    // =========================
    public Map<String, Object> getAllProducts() {
        Long customerId = jwtUtils.getRequiredCustomerId();

        // ✅ Only products belonging to the same customer
        List<Product> products = productRepository.findByCustomerId(customerId);

        List<ProductDTO> list = products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return Map.of(
                "message", "Products fetched successfully",
                "data", list
        );
    }

    // =========================
    // READ BY ID
    // =========================
    public Map<String, Object> getProductById(Long id) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Product product = productRepository.findByProductIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new RuntimeException("Product not found or unauthorized access"));

        return Map.of(
                "message", "Product fetched successfully",
                "data", mapToDTO(product)
        );
    }

    // =========================
    // READ BY DESIGN CODE
    // =========================
    public Map<String, Object> getProductByDesignCode(String designCode) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Product product = productRepository.findByDesignCodeAndCustomerId(designCode, customerId)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found for your account with design code: " + designCode));

        return Map.of(
                "message", "Product fetched successfully by design code",
                "data", mapToDTO(product)
        );
    }

    // =========================
    // UPDATE
    // =========================
    public Map<String, Object> updateProduct(Long id, ProductDTO dto) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Product existing = productRepository.findByProductIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new RuntimeException("Product not found or unauthorized access"));

        existing.setProductName(dto.getProductName());
        existing.setDesignCode(dto.getDesignCode());
        existing.setPattern(dto.getPattern());
        existing.setImageUrl(dto.getImageUrl());

        if (dto.getBrandId() != null) {
            Brand brand = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with ID: " + dto.getBrandId()));
            existing.setBrand(brand);
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        if (dto.getClothTypeId() != null) {
            ClothType clothType = clothTypeRepository.findById(dto.getClothTypeId())
                    .orElseThrow(() -> new RuntimeException("Cloth Type not found with ID: " + dto.getClothTypeId()));
            existing.setClothType(clothType);
        }

        Product updated = productRepository.save(existing);

        return Map.of(
                "message", "Product updated successfully",
                "data", mapToDTO(updated)
        );
    }

    // =========================
    // DELETE
    // =========================
    public Map<String, Object> deleteProduct(Long id) {
        Long customerId = jwtUtils.getRequiredCustomerId();

        Product product = productRepository.findByProductIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new RuntimeException("Product not found or unauthorized access"));

        productRepository.delete(product);

        return Map.of("message", "Product deleted successfully with ID: " + id);
    }
}
