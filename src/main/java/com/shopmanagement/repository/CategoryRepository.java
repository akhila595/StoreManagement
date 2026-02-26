package com.shopmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopmanagement.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findByCustomer_IdAndStatus(Long customerId, String status);
    Optional<Category> findByCategoryNameAndCustomer_Id(String categoryName, Long customerId);
    Optional<Category> findByCategoryIdAndCustomer_Id(Long categoryId, Long customerId);
    boolean existsByCategoryName(String categoryName);
}
