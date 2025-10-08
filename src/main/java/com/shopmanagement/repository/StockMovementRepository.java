package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.StockMovement;
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
