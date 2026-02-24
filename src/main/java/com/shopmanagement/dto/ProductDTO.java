package com.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product DTO for SaaS Inventory System
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    // Core fields
    private String name;
    private String code;
    private String imageUrl;

    // Brand
    private Long brandId;
    private String brandName; // read-only

    // Category
    private Long categoryId;
    private String categoryName; // read-only
}