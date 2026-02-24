package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByProductSkuAndCustomer_Id(String productSku, Long customerId);

    boolean existsByProductSkuAndCustomer_Id(String productSku, Long customerId);

    List<ProductVariant> findByProduct_IdAndCustomer_Id(Long productId, Long customerId);

    Optional<ProductVariant> findByIdAndCustomer_Id(Long id, Long customerId);

    List<ProductVariant> findByStockQtyLessThanAndCustomer_Id(int threshold, Long customerId);
}