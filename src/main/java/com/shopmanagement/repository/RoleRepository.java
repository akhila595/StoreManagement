package com.shopmanagement.repository;

import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByIdAndCustomer_Id(Long id, Long customerId);
    List<Role> findByCustomer_Id(Long customerId);
	Optional<Role> findByNameAndCustomerIsNull(String name);
	
}
