package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.ClothType;

public interface ClothTypeRepository extends JpaRepository<ClothType, Long> {
	
}