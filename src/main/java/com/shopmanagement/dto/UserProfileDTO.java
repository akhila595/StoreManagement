package com.shopmanagement.dto;

import java.util.List;
import java.util.Set;

public class UserProfileDTO {

    private String name;
    private String email;
    private Set<String> roles;
    private List<String> permissions;
    private Long customerId; // ✅ Added for tenant identification

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
