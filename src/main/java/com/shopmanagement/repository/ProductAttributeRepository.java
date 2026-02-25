package com.shopmanagement.repository;

import com.shopmanagement.model.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    List<ProductAttribute> findByProduct_ProductIdAndCustomer_Id(
            Long productId, Long customerId);

    Optional<ProductAttribute> findByProduct_ProductIdAndAttribute_IdAndCustomer_Id(
            Long productId, Long attributeId, Long customerId);

    Optional<ProductAttribute> findByIdAndCustomer_Id(Long id, Long customerId);

	List<ProductAttribute> findByProduct_IdAndCustomer_Id(Long productId, Long customerId);
}