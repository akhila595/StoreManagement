package com.shopmanagement.service;

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

    public List<Category> getAllCategories() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        return categoryRepository
                .findByCustomer_IdAndStatus(customerId, "ACTIVE");
    }

    public Category addCategory(Category category) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Duplicate check
        categoryRepository
                .findByCategoryNameAndCustomer_Id(category.getCategoryName(), customerId)
                .ifPresent(c -> {
                    throw new RuntimeException("Category already exists.");
                });

        category.setCustomer(customer);
        category.setStatus("ACTIVE");

        return categoryRepository.save(category);
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

    public List<Brand> getAllBrands() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        return brandRepository
                .findByCustomer_IdAndStatus(customerId, "ACTIVE");
    }

    public Brand addBrand(Brand brand) {

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

        return brandRepository.save(brand);
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