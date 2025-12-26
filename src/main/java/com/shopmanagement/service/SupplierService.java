package com.shopmanagement.service;

import com.shopmanagement.dto.SupplierDTO;
import com.shopmanagement.model.Customer;
import com.shopmanagement.model.Supplier;
import com.shopmanagement.repository.CustomerRepository;
import com.shopmanagement.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtUtils jwtUtil;

    /** ✅ Get all suppliers for the logged-in customer */
    public List<Supplier> getAllSuppliers() {
        Long customerId = jwtUtil.getRequiredCustomerId();
        return supplierRepository.findByCustomer_Id(customerId);
    }

    /** ✅ Save or update supplier (scoped to customer) */
    public Supplier saveOrUpdate(SupplierDTO dto) {
        Long customerId = jwtUtil.getRequiredCustomerId();

        Supplier supplier;

        if (dto.getSupplierId() != null) {
            // Update existing supplier, ensuring same customer
            supplier = supplierRepository.findBySupplierIdAndCustomer_Id(dto.getSupplierId(), customerId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found or unauthorized"));
        } else {
            // Create new supplier
            supplier = new Supplier();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            supplier.setCustomer(customer);
        }

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

    /** ✅ Delete supplier (scoped to customer) */
    public void deleteSupplier(Long supplierId) {
        Long customerId = jwtUtil.getRequiredCustomerId();

        Supplier supplier = supplierRepository.findBySupplierIdAndCustomer_Id(supplierId, customerId)
                .orElseThrow(() -> new RuntimeException("Supplier not found or unauthorized"));

        supplierRepository.delete(supplier);
    }
}
