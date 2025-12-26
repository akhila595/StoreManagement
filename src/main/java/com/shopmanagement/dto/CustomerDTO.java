package com.shopmanagement.dto;

/**
 * Data Transfer Object for Customer
 * Used for sending and receiving customer data across the application.
 */
public class CustomerDTO {

    private Long id;
    private String customerName;
    private String email;
    private String phone;
    private String address;
    private String gstNumber;

    // ✅ Default constructor
    public CustomerDTO() {
    }

    // ✅ Full constructor (matches service mapping)
    public CustomerDTO(Long id, String customerName, String email, String phone, String address, String gstNumber) {
        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gstNumber = gstNumber;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
