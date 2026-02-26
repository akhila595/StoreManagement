package com.shopmanagement.repository;

import com.shopmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /* ================= BASIC FETCH ================= */

    // Fetch all products for logged-in customer
    List<Product> findByCustomer_IdAndStatus(String status, Long customerId);

    // Fetch by ID (secure multi-tenant)
    Optional<Product> findByIdAndCustomer_Id(Long productId, Long customerId);

    /* ================= UNIQUE VALIDATIONS ================= */

    // Prevent duplicate product code per customer
    Optional<Product> findByCodeAndCustomer_Id(String code, Long customerId);

    boolean existsByCodeAndCustomer_Id(String code, Long customerId);

    // Optional: prevent duplicate product name per customer
    Optional<Product> findByNameAndCustomer_Id(String name, Long customerId);

    boolean existsByNameAndCustomer_Id(String name, Long customerId);

  

}