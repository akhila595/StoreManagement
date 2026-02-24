package com.shopmanagement.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopmanagement.model.VariantAttribute;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {

    List<VariantAttribute> findByVariant_IdAndCustomer_Id(Long variantId, Long customerId);

    List<VariantAttribute> findByAttributeValue_IdAndCustomer_Id(Long attributeValueId, Long customerId);
}