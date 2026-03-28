package com.shopmanagement.dto;

import java.util.List;

public class AttributeReportDTO {
	
	private String attributeName;
    private List<String> values;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
