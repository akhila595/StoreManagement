package com.shopmanagement.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopmanagement.model.ProductAttribute;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    List<ProductAttribute> findByProduct_IdAndCustomer_Id(Long productId, Long customerId);

    boolean existsByProduct_IdAndAttribute_IdAndCustomer_Id(
            Long productId, Long attributeId, Long customerId);
}