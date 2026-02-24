package com.shopmanagement.repository;

import com.shopmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Fetch all products of logged-in customer
    List<Product> findByCustomer_Id(Long customerId);

    // Secure fetch by ID
    Optional<Product> findByIdAndCustomer_Id(Long productId, Long customerId);

    // Prevent duplicate product code per customer
    Optional<Product> findByCodeAndCustomer_Id(String code, Long customerId);

    // Optional: prevent duplicate name per customer
    Optional<Product> findByNameAndCustomer_Id(String name, Long customerId);
}