package com.shopmanagement.dto;

import lombok.Data;

@Data
public class AttributeValueDTO {
    private Long id;
    private Long attributeId;
    private String value;
    private String code;
}