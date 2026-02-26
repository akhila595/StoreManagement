package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrand(String brand);
    boolean existsByBrand(String brand);
    List<Brand> findByCustomer_IdAndStatus(Long customerId, String status);
    Optional<Brand> findByBrandAndCustomer_Id(String brand, Long customerId);
    Optional<Brand> findByIdAndCustomer_Id(Long brandId, Long customerId);
}
