package com.shopmanagement.dto;

import java.util.Set;

public class UserDTO {

	private Long id;
	private String name;
	private String email;
	private String password;
	private Set<String> roleNames;
	private Long customerId; // ✅ Added for tenant context
	private String profileImage;

	public UserDTO() {
	}

	public UserDTO(String name, String email, Set<String> roleNames, String password, Long customerId) {
		this.name = name;
		this.email = email;
		this.roleNames = roleNames;
		this.password = password;
		this.customerId = customerId;
	}

	public UserDTO(Long id, String name, String email, Set<String> roleNames, String password, Long customerId) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.roleNames = roleNames;
		this.password = password;
		this.customerId = customerId;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(Set<String> roleNames) {
		this.roleNames = roleNames;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

}
