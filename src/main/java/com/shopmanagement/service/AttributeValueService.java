package com.shopmanagement.service;

import com.shopmanagement.dto.AttributeValueDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AttributeValueService {

    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtils jwtUtils;

    public AttributeValueService(AttributeRepository attributeRepository,
                                 AttributeValueRepository attributeValueRepository,
                                 CustomerRepository customerRepository,
                                 JwtUtils jwtUtils) {
        this.attributeRepository = attributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.customerRepository = customerRepository;
        this.jwtUtils = jwtUtils;
    }

    /* ================= CREATE ================= */

    public String createAttributeValue(AttributeValueDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        if (dto.getValue() == null || dto.getValue().trim().isEmpty()) {
            throw new RuntimeException("Value cannot be empty.");
        }

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(dto.getAttributeId(), customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        attributeValueRepository
                .findByValueAndAttribute_IdAndCustomer_Id(
                        dto.getValue().trim(),
                        dto.getAttributeId(),
                        customerId)
                .ifPresent(v -> {
                    throw new RuntimeException("Value already exists for this attribute.");
                });

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        AttributeValue value = new AttributeValue();
        value.setValue(dto.getValue().trim());
        value.setCode(dto.getCode() != null
                ? dto.getCode().trim().toUpperCase()
                : null);
        value.setAttribute(attribute);
        value.setCustomer(customer);

        attributeValueRepository.save(value);

        return "Attribute value created successfully";
    }

    /* ================= READ ================= */

    @Transactional(readOnly = true)
    public List<AttributeValue> getValuesByAttribute(Long attributeId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        return attributeValueRepository
                .findByAttribute_IdAndCustomer_Id(attributeId, customerId);
    }

    /* ================= UPDATE ================= */

    public String updateAttributeValue(Long id, AttributeValueDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        AttributeValue value = attributeValueRepository
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Value not found"));

        value.setValue(dto.getValue().trim());
        value.setCode(dto.getCode() != null
                ? dto.getCode().trim().toUpperCase()
                : null);

        attributeValueRepository.save(value);

        return "Attribute value updated successfully";
    }

    /* ================= DELETE ================= */

    public String deleteAttributeValue(Long id) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        AttributeValue value = attributeValueRepository
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Value not found"));

        attributeValueRepository.delete(value);

        return "Attribute value deleted successfully";
    }
}