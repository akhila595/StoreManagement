package com.shopmanagement.service;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.model.Brand;
import com.shopmanagement.model.Category;
import com.shopmanagement.model.ClothType;
import com.shopmanagement.model.Product;
import com.shopmanagement.repository.BrandRepository;
import com.shopmanagement.repository.CategoryRepository;
import com.shopmanagement.repository.ClothTypeRepository;
import com.shopmanagement.repository.ProductRepository;
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

    // --- Helper: map entity → DTO ---
    private ProductDTO mapToDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getProductId());
        dto.setProductName(p.getProductName());
        dto.setDesignCode(p.getDesignCode());
        dto.setPattern(p.getPattern());
        dto.setImageUrl(
            (p.getImageUrl() != null && !p.getImageUrl().isEmpty())
            ? p.getImageUrl()
            : "/uploads/products/no-image.png" // placeholder if image is missing
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

        return dto;
    }

    // --- Helper: map DTO → entity ---
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

        return product;
    }

    // --- CREATE ---
    public Map<String, Object> createProduct(ProductDTO dto) {
        Product product = mapToEntity(dto);
        Product saved = productRepository.save(product);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");
        response.put("data", mapToDTO(saved));
        return response;
    }

    // --- READ ALL ---
    public Map<String, Object> getAllProducts() {
        List<ProductDTO> list = productRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Products fetched successfully");
        response.put("data", list);
        return response;
    }

    // --- READ BY ID ---
    public Map<String, Object> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product fetched successfully");
        response.put("data", mapToDTO(product));
        return response;
    }

    // --- READ BY DESIGN CODE ---
    public Map<String, Object> getProductByDesignCode(String designCode) {
        Product product = productRepository.findByDesignCode(designCode)
                .orElseThrow(() -> new RuntimeException("Product not found with design code: " + designCode));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product fetched successfully by design code");
        response.put("data", mapToDTO(product));
        return response;
    }

    // --- UPDATE ---
    public Map<String, Object> updateProduct(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

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
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product updated successfully");
        response.put("data", mapToDTO(updated));
        return response;
    }

    // --- DELETE ---
    public Map<String, Object> deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product deleted successfully with ID: " + id);
        return response;
    }
}
