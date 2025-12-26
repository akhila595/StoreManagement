package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.ProductVariant;
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
	
	 Optional<ProductVariant> findByProductSku(String productSku);
	 List<ProductVariant> findByStockQtyLessThan(int stockQty);
	 Optional<ProductVariant> findByProductSkuAndCustomer_Id(String sku, Long customerId);
	    List<ProductVariant> findByStockQtyLessThanAndCustomer_Id(int threshold, Long customerId);
}
