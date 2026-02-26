package com.shopmanagement.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_name", "customer_id"})
        }
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

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

    public Long getCategoryId() { return categoryId; }

    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeletedAt() { return deletedAt; }

    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}