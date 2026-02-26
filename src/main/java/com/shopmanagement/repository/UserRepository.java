package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopmanagement.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndCustomer_Id(Long id, Long customerId);
    
    boolean existsByEmail(String email);

    /* ================= LOGIN ================= */

    Optional<User> findByEmailAndStatus(String email, String status);

    Optional<User> findByEmailAndCustomer_IdAndStatus(
            String email, Long customerId, String status);

    boolean existsByEmailAndCustomer_Id(
            String email, Long customerId);

    /* ================= CUSTOMER USERS ================= */

    List<User> findByCustomer_Id(Long customerId);

    List<User> findByCustomer_IdAndStatus(
            Long customerId, String status);

    Optional<User> findByIdAndCustomer_IdAndStatus(
            Long userId, Long customerId, String status);

    /* ================= GLOBAL SUPERADMIN ================= */

    Optional<User> findByEmailAndCustomerIsNullAndStatus(
            String email, String status);
	List<User> findByStatus(String string);
}
