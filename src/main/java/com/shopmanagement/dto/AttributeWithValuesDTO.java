package com.shopmanagement.dto;

import java.util.List;

public class AttributeWithValuesDTO {
	 private Long attributeId;
	    private String attributeName;
	    private List<AttributeValueDTO> values;
		public Long getAttributeId() {
			return attributeId;
		}
		public void setAttributeId(Long attributeId) {
			this.attributeId = attributeId;
		}
		public String getAttributeName() {
			return attributeName;
		}
		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}
		public List<AttributeValueDTO> getValues() {
			return values;
		}
		public void setValues(List<AttributeValueDTO> values) {
			this.values = values;
		}
}
