package com.shopmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
	Optional<Brand> findByBrand(String brandName);
}