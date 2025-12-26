package com.shopmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.SaleItem;
@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

	List<SaleItem> findBySaleInvoice_SaleDate(LocalDate saleDate);
    List<SaleItem> findBySaleInvoice_SaleDateBetween(LocalDate start, LocalDate end);
    List<SaleItem> findBySaleInvoice_SaleDateAndCustomer_Id(LocalDate date, Long customerId);
    List<SaleItem> findBySaleInvoice_SaleDateBetweenAndCustomer_Id(LocalDate start, LocalDate end, Long customerId);
}
