package com.shopmanagement.dto;

public class AttributeValueResponseDTO {

    private Long id;
    private Long attributeId;
    private String value;
    private String code;

    public AttributeValueResponseDTO() {}

    public AttributeValueResponseDTO(Long id, Long attributeId, String value, String code) {
        this.id = id;
        this.attributeId = attributeId;
        this.value = value;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCode(String code) {
        this.code = code;
    }
}