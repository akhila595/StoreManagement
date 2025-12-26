package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.Supplier;
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
	List<Supplier> findByCustomer_Id(Long customerId);

    Optional<Supplier> findBySupplierIdAndCustomer_Id(Long supplierId, Long customerId);

    Optional<Supplier> findBySupplierNameAndCustomer_Id(String supplierName, Long customerId);
}
