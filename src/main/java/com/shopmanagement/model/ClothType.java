package com.shopmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cloth_types")
public class ClothType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clothType;

    public ClothType() {}

    public ClothType(String clothType) {
        this.clothType = clothType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClothType() { return clothType; }
    public void setClothType(String clothType) { this.clothType = clothType; }
}
