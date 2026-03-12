package com.shopmanagement.service;

import com.shopmanagement.dto.BrandDTO;
import com.shopmanagement.dto.CategoryDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MasterDataService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private JwtUtils jwtUtils;

    /* ==========================================================
       ===================== CATEGORY ============================
       ========================================================== */

    public List<CategoryDTO> getAllCategories() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Category> categories   = categoryRepository
                .findByCustomer_IdAndStatus(customerId, "ACTIVE");
         
         return categories.stream().map(category -> {
             CategoryDTO dto = new CategoryDTO();
             dto.setCategoryId(category.getCategoryId());
             dto.setCategoryName(category.getCategoryName());
             dto.setStatus(category.getStatus());
             dto.setCreatedAt(category.getCreatedAt());
             return dto;
         }).toList();
    }

    public String addCategory(Category category) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        categoryRepository
                .findByCategoryNameAndCustomer_Id(category.getCategoryName(), customerId)
                .ifPresent(c -> {
                    throw new RuntimeException("Category already exists.");
                });

        category.setCustomer(customer);
        category.setStatus("ACTIVE");

        categoryRepository.save(category);   // ⭐ MISSING LINE

        return "Category created successfully";
    }
    
    public String deleteCategory(Long categoryId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Category category = categoryRepository
                .findByCategoryIdAndCustomer_Id(categoryId, customerId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setStatus("DELETED");
        categoryRepository.save(category);

        return "Category deleted successfully";
    }

    /* ==========================================================
       ===================== BRAND ===============================
       ========================================================== */

    public List<BrandDTO> getAllBrands() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Brand> brands= brandRepository
                .findByCustomer_IdAndStatus(customerId, "ACTIVE");
         
         return brands.stream().map(brand -> {
             BrandDTO dto = new BrandDTO();
             dto.setId(brand.getId());
             dto.setBrand(brand.getBrand());
             return dto;
         }).toList();
    }

    public String addBrand(Brand brand) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        brandRepository
                .findByBrandAndCustomer_Id(brand.getBrand(), customerId)
                .ifPresent(b -> {
                    throw new RuntimeException("Brand already exists.");
                });

        brand.setCustomer(customer);
        brand.setStatus("ACTIVE");

        brandRepository.save(brand);   // ⭐ MISSING LINE

        return "Brand created successfully";
    }

    public String deleteBrand(Long brandId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Brand brand = brandRepository
                .findByIdAndCustomer_Id(brandId, customerId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setStatus("DELETED");
        brandRepository.save(brand);

        return "Brand deleted successfully";
    }
}