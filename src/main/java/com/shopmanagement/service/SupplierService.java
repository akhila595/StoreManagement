package com.shopmanagement.service;

import com.shopmanagement.dto.SupplierDTO;
import com.shopmanagement.model.Supplier;
import com.shopmanagement.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier saveOrUpdate(SupplierDTO dto) {
        Supplier supplier = new Supplier();

        if (dto.getSupplierId() != null)
            supplier.setSupplierId(dto.getSupplierId());

        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhoneNumber(dto.getPhoneNumber());
        supplier.setWhatsApp(dto.getWhatsApp());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setGstNumber(dto.getGstNumber());
        supplier.setPaymentTerms(dto.getPaymentTerms());
        supplier.setNotes(dto.getNotes());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long supplierId) {
        supplierRepository.deleteById(supplierId);
    }
}
