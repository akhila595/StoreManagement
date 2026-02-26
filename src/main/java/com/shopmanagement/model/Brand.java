package com.shopmanagement.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"brand", "customer_id"})
        }
)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    /* ================= MULTI TENANT ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /* ================= SOFT DELETE ================= */

    @Column(nullable = false)
    private String status = "ACTIVE";

    private LocalDateTime deletedAt;

    /* ================= AUDIT ================= */

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markDeleted() {
        this.status = "DELETED";
        this.deletedAt = LocalDateTime.now();
    }

    // Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }

    public void setBrand(String brand) { this.brand = brand; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeletedAt() { return deletedAt; }

    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}