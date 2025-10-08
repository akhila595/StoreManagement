package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.SaleInvoice;
@Repository
public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, String>{

}
