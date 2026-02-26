package com.shopmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
    name = "role",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "customer_id"})
    }
)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= BASIC INFO ================= */

    @Column(nullable = false)
    private String name;   // ADMIN, MANAGER, USER

    private String description;

    /* ================= PERMISSIONS ================= */

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /* ================= TENANT ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
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
        if (this.name != null) {
            this.name = this.name.toUpperCase();
        }
    }

    /* ================= CONSTRUCTORS ================= */

    public Role() {}

    public Role(String name, String description) {
        this.name = name != null ? name.toUpperCase() : null;
        this.description = description;
    }

    /* ================= SOFT DELETE METHOD ================= */

    public void markDeleted() {
        this.status = "DELETED";
        this.deletedAt = LocalDateTime.now();
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.toUpperCase() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /* ================= equals & hashCode ================= */

    public void setId(Long id) {
		this.id = id;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}