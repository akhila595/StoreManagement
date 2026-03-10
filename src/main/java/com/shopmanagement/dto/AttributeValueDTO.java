package com.shopmanagement.dto;

import java.util.List;

public class AttributeValueDTO {
    private Long id;
    private Long attributeId;
    private List<String> values;
    private String code;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Long attributeId) {
		this.attributeId = attributeId;
	}
	
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}