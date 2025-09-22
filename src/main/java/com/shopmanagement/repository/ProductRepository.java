package com.shopmanagement.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByDesignCode(String designCode);
    
}
