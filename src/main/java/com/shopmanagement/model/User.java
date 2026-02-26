package com.shopmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "customer_id"})
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    /* ================= TENANT ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /* ================= ROLES ================= */

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /* ================= PROFILE ================= */

    @Column(name = "profile_image")
    private String profileImage;

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

    /* ================= SOFT DELETE METHOD ================= */

    public void markDeleted() {
        this.status = "DELETED";
        this.deletedAt = LocalDateTime.now();
    }

    /* ================= GETTERS / SETTERS ================= */

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

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

	public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /* ================= FRONTEND HELPER ================= */

    public Set<String> getRoleNames() {
        Set<String> list = new HashSet<>();
        for (Role r : roles) {
            if ("ACTIVE".equals(r.getStatus())) {
                list.add(r.getName());
            }
        }
        return list;
    }
}