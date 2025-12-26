package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.dto.CustomerDTO;
import com.shopmanagement.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 🔹 Find by customer name (unique)
    Optional<Customer> findByCustomerName(String customerName);

    // 🔹 Find by email (optional unique field)
    Optional<Customer> findByEmail(String email);

    // 🔹 Find by phone number (if you allow lookup by phone)
    Optional<Customer> findByPhone(String phone);

    // 🔹 Get all active customers
    List<Customer> findByStatus(String status);

    // 🔹 Check if customer name already exists
    boolean existsByCustomerName(String customerName);

    // 🔹 Check if email already exists (if used for login or contact)
    boolean existsByEmail(String email);

    // 🔹 Search by name or email (for SuperAdmin search filters)
    List<Customer> findByCustomerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    // 🔹 Get all customers ordered by newest first (useful for dashboards)
    List<Customer> findAllByOrderByCreatedAtDesc();

    // 🔹 Get customers created recently (e.g., for report or dashboard)
    List<Customer> findTop10ByOrderByCreatedAtDesc();

    // 🔹 Soft-delete support (if you mark customers as INACTIVE instead of deleting)
    List<Customer> findByStatusNot(String status);
   
}
