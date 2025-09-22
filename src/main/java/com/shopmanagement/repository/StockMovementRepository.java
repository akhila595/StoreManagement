package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
