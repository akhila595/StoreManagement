package com.shopmanagement.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., "PRODUCT_READ", "USER_MANAGE"

    @Column(nullable = false)
    private String name; // Human readable name

    private String description; // Optional field for clarity

    // ======== Constructors ========
    public Permission() {}

    // Used when inserting base permissions without description
    public Permission(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Used when seeding with description (DataInitializer)
    public Permission(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    // ======== Getters & Setters ========
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ======== equals & hashCode ========
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
