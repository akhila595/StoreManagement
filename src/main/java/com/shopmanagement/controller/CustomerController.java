package com.shopmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopmanagement.dto.CustomerDTO;
import com.shopmanagement.dto.CustomerRegistrationRequest;
import com.shopmanagement.service.CustomerService;

@RestController
@RequestMapping("/api/superadmin")
@CrossOrigin
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ==========================================================
    // 🟢 CREATE CUSTOMER + ADMIN
    // ==========================================================
    @PostMapping("/create-customer")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRegistrationRequest request) {
        try {
            CustomerDTO customer = customerService.createCustomerWithAdmin(request);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Failed: " + e.getMessage());
        }
    }

    // ==========================================================
    // 🟡 GET ALL CUSTOMERS (returns DTO list)
    // ==========================================================
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        try {
            List<CustomerDTO> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================================
    // 🔵 GET CUSTOMER BY ID
    // ==========================================================
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customerId") Long id) {
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    // ==========================================================
    // 🟠 UPDATE CUSTOMER DETAILS
    // ==========================================================
    @PutMapping("/customers/{customerId}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable("customerId") Long id,
            @RequestBody CustomerDTO updated
    ) {
        try {
            CustomerDTO customer = customerService.updateCustomer(id, mapToEntity(updated));
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    // ==========================================================
    // 🔴 DELETE CUSTOMER
    // ==========================================================
    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("customerId") Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok("✅ Customer deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    // ==========================================================
    // 🔧 HELPER — Convert DTO → Entity (for updates)
    // ==========================================================
    private com.shopmanagement.model.Customer mapToEntity(CustomerDTO dto) {
        com.shopmanagement.model.Customer c = new com.shopmanagement.model.Customer();
        c.setCustomerName(dto.getCustomerName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setAddress(dto.getAddress());
        c.setGstNumber(dto.getGstNumber());
        return c;
    }
}
