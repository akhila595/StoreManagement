package com.shopmanagement.dto;

import java.util.List;

public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private List<Long> permissionIds;

    // Default constructor
    public RoleDTO() {}

    // Constructor for creating new RoleDTO (without ID)
    public RoleDTO(String name, String description, List<Long> permissionIds) {
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    // Constructor for updating an existing RoleDTO (with ID)
    public RoleDTO(Long id, String name, String description, List<Long> permissionIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<Long> permissionIds) { this.permissionIds = permissionIds; }
}
