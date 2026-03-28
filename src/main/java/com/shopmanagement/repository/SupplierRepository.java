package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.Supplier;
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
	
	@Query("SELECT s FROM Supplier s JOIN FETCH s.customer WHERE s.customer.id = :customerId")
	List<Supplier> getSuppliersWithCustomer(@Param("customerId") Long customerId);
	
	List<Supplier> findByCustomer_Id(Long customerId);
	
    Optional<Supplier> findBySupplierIdAndCustomer_Id(Long supplierId, Long customerId);

    Optional<Supplier> findBySupplierNameAndCustomer_Id(String supplierName, Long customerId);
}
