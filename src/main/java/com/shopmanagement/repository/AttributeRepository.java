package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopmanagement.model.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    List<Attribute> findByCustomer_Id(Long customerId);

    Optional<Attribute> findByIdAndCustomer_Id(Long id, Long customerId);

    Optional<Attribute> findByNameAndCustomer_Id(String name, Long customerId);
}