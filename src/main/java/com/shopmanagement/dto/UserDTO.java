package com.shopmanagement.dto;

import java.util.Set;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;

    // NEW FIELD — list of role names
    private Set<String> roleNames;

    // Default constructor
    public UserDTO() {
    }

    // Constructor for creating a new UserDTO (without id)
    public UserDTO(String name, String email, Set<String> roleNames, String password) {
        this.name = name;
        this.email = email;
        this.roleNames = roleNames;
        this.password = password;
    }

    // Constructor for updating an existing UserDTO (with id)
    public UserDTO(Long id, String name, String email, Set<String> roleNames, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleNames = roleNames;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
