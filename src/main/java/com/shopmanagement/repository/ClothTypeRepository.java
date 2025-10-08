package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.ClothType;
@Repository
public interface ClothTypeRepository extends JpaRepository<ClothType, Long> {
	
}