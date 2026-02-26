package com.shopmanagement.repository;

import com.shopmanagement.model.Customer;
import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByIdAndCustomer_Id(Long id, Long customerId);
	/* ========== GLOBAL ROLES ========== */

    Optional<Role> findByNameAndCustomerIsNull(String name);

    Optional<Role> findByNameAndCustomerIsNullAndStatus(
            String name, String status);

    /* ========== TENANT ROLES ========== */

    Optional<Role> findByNameAndCustomer_IdAndStatus(
            String name, Long customerId, String status);

    List<Role> findByCustomer_IdAndStatus(
            Long customerId, String status);

    boolean existsByNameAndCustomer(
            String name, Customer customer);

    boolean existsByNameAndCustomer_Id(
            String name, Long customerId);

    List<Role> findByCustomer_Id(Long customerId);
}
