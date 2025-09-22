package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
	
	 Optional<ProductVariant> findByProductSku(String productSku);
	 List<ProductVariant> findByStockQtyLessThan(int stockQty);
}
