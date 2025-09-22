package com.shopmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopmanagement.model.ProductVariant;
import com.shopmanagement.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
	List<Purchase> findByPurchaseDate(LocalDate purchaseDate);
    List<Purchase> findByPurchaseDateBetween(LocalDate start, LocalDate end);
    List<Purchase> findBySupplier_SupplierIdAndPurchaseDateBetween(Long supplierId, LocalDate start, LocalDate end);
    Optional<Purchase> findTopByProductVariantOrderByPurchaseDateDesc(ProductVariant productVariant);

}
