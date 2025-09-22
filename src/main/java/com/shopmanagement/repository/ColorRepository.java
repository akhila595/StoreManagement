package com.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmanagement.model.Color;

public interface ColorRepository extends JpaRepository<Color, Long> {}