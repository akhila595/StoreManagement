package com.shopmanagement.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;

@Data
public class CustomerRegistrationRequest {

	// 🟦 Customer Details
	private String name;
	private String email;
	private String phone;
	private String address;
	private String gstNumber;

	// 🟩 Admin User Details
	private String adminName;
	private String adminEmail;
	private String adminPassword;
	
	public CustomerRegistrationRequest() {
		
	}
	public CustomerRegistrationRequest(String name, String email, String phone, String address, String gstNumber,
			String adminName, String adminEmail, String adminPassword) {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.gstNumber = gstNumber;
		this.adminName = adminName;
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
}
