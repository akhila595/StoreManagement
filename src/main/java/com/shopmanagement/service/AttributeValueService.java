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


    public String createAttributeValues(AttributeValueDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            throw new RuntimeException("Values cannot be empty.");
        }

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(dto.getAttributeId(), customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        for (String val : dto.getValues()) {

            if (val == null || val.trim().isEmpty()) {
                throw new RuntimeException("Attribute value cannot be empty");
            }

            String value = val.trim();

            attributeValueRepository
                    .findByValueAndAttribute_IdAndCustomer_Id(
                            value,
                            dto.getAttributeId(),
                            customerId)
                    .ifPresent(v -> {
                        throw new RuntimeException("Value already exists: " + value);
                    });

            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setValue(value);

            attributeValue.setCode(generateCodeFromValue(value));

            attributeValue.setAttribute(attribute);
            attributeValue.setCustomer(customer);

            attributeValueRepository.save(attributeValue);
        }

        return "Attribute values created successfully";
    }
    /* ================= READ ================= */

    @Transactional(readOnly = true)
    public List<AttributeValue> getValuesByAttribute(Long attributeId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        return attributeValueRepository
                .findByAttribute_IdAndCustomer_Id(attributeId, customerId);
    }

    /* ================= UPDATE ================= */


    public String addValuesToAttribute(AttributeValueDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            throw new RuntimeException("Values cannot be empty.");
        }

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(dto.getAttributeId(), customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        for (String val : dto.getValues()) {

            if (val == null || val.trim().isEmpty()) {
                throw new RuntimeException("Value cannot be empty");
            }

            String value = val.trim();

            attributeValueRepository
                    .findByValueAndAttribute_IdAndCustomer_Id(
                            value,
                            dto.getAttributeId(),
                            customerId)
                    .ifPresent(v -> {
                        throw new RuntimeException("Value already exists: " + value);
                    });

            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setValue(value);
            attributeValue.setCode(generateCodeFromValue(value));
            attributeValue.setAttribute(attribute);
            attributeValue.setCustomer(customer);

            attributeValueRepository.save(attributeValue);
        }

        return "Values added successfully";
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
    
    private String generateCodeFromValue(String value) {
        String clean = value.trim().toUpperCase().replaceAll("\\s+", "");

        if (clean.length() >= 2) {
            return clean.substring(0, 2);
        }
        return clean;
    }
}