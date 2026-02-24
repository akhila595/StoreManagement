package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopmanagement.model.AttributeValue;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    List<AttributeValue> findByAttribute_IdAndCustomer_Id(Long attributeId, Long customerId);

    Optional<AttributeValue> findByIdAndCustomer_Id(Long id, Long customerId);

    Optional<AttributeValue> findByValueAndAttribute_IdAndCustomer_Id(
            String value, Long attributeId, Long customerId);
}