package com.shopmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier")
public class Supplier {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long supplierId;
	    private String supplierName;
	    private String contactInfo;

	    public Long getSupplierId() {
	        return supplierId;
	    }

	    public void setSupplierId(Long supplierId) {
	        this.supplierId = supplierId;
	    }

	    public String getSupplierName() {
	        return supplierName;
	    }

	    public void setSupplierName(String supplierName) {
	        this.supplierName = supplierName;
	    }

	    public String getContactInfo() {
	        return contactInfo;
	    }

	    public void setContactInfo(String contactInfo) {
	        this.contactInfo = contactInfo;
	    }
}
