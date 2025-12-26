package com.shopmanagement.repository;

import com.shopmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCustomerId(Long customerId);
    Optional<Product> findByProductIdAndCustomerId(Long id, Long customerId);
    Optional<Product> findByDesignCodeAndCustomerId(String designCode, Long customerId);
    Optional<Product> findByDesignCode(String designCode);
}
