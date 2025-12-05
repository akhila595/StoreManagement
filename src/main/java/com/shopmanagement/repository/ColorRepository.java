package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shopmanagement.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    boolean existsByColor(String color);
}
