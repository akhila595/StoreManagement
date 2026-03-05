package com.shopmanagement.controller;

import com.shopmanagement.dto.AttributeValueDTO;
import com.shopmanagement.dto.AttributeValueResponseDTO;
import com.shopmanagement.model.AttributeValue;
import com.shopmanagement.service.AttributeValueService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attribute-values")
public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    public AttributeValueController(AttributeValueService attributeValueService) {
        this.attributeValueService = attributeValueService;
    }

    /* ================= CREATE ================= */

    @PostMapping
    public String create(@RequestBody AttributeValueDTO dto) {

        return attributeValueService.createAttributeValue(dto);
    }

    /* ================= GET VALUES BY ATTRIBUTE ================= */

    @GetMapping("/attribute/{attributeId}")
    public List<AttributeValueResponseDTO> getValues(@PathVariable Long attributeId) {

        return attributeValueService.getValuesByAttribute(attributeId)
                .stream()
                .map(v -> new AttributeValueResponseDTO(
                        v.getId(),
                        v.getAttribute().getId(),
                        v.getValue(),
                        v.getCode()
                ))
                .collect(Collectors.toList());
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody AttributeValueDTO dto) {

        return attributeValueService.updateAttributeValue(id, dto);
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        return attributeValueService.deleteAttributeValue(id);
    }
}