package com.shopmanagement.dto;

import java.util.Set;
import java.util.List;

public class LoginResponseDTO {
    private String token;
    private String name;
    private String email;
    private Set<String> roles;            // role names
    private List<String> permissions;     // optional: flattened permission codes (if you choose option A)
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
}
