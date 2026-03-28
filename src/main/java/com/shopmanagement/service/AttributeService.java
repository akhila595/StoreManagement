package com.shopmanagement.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmanagement.dto.AttributeDTO;
import com.shopmanagement.dto.AttributeReportDTO;
import com.shopmanagement.dto.AttributeResponseDTO;
import com.shopmanagement.dto.AttributeValueDTO;
import com.shopmanagement.dto.AttributeWithValuesDTO;
import com.shopmanagement.model.Attribute;
import com.shopmanagement.model.AttributeValue;
import com.shopmanagement.model.Customer;
import com.shopmanagement.repository.AttributeRepository;
import com.shopmanagement.repository.AttributeValueRepository;
import com.shopmanagement.repository.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final CustomerRepository customerRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final JwtUtils jwtUtils;

    public AttributeService(AttributeRepository attributeRepository,
                            CustomerRepository customerRepository,
                            AttributeValueRepository attributeValueRepository,
                            JwtUtils jwtUtils) {
        this.attributeRepository = attributeRepository;
        this.customerRepository = customerRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.jwtUtils = jwtUtils;
    }

    /* ================= CREATE ================= */

    public Map<String, Object> createAttribute(AttributeDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("Attribute name cannot be empty.");
        }

        attributeRepository.findByNameAndCustomer_Id(dto.getName().trim(), customerId)
                .ifPresent(a -> {
                    throw new RuntimeException("Attribute already exists.");
                });

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Attribute attribute = new Attribute();
        attribute.setName(dto.getName().trim());
        attribute.setCustomer(customer);

        Attribute saved = attributeRepository.save(attribute);

        AttributeResponseDTO response =
                new AttributeResponseDTO(saved.getId(), saved.getName());

        return Map.of(
                "message", "Attribute created",
                "data", response
        );
    }

    /* ================= READ ================= */

    @Transactional(readOnly = true)
    public List<AttributeResponseDTO> getAllAttributes() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        return attributeRepository.findByCustomer_Id(customerId)
                .stream()
                .map(a -> new AttributeResponseDTO(a.getId(), a.getName()))
                .collect(Collectors.toList());
    }

    /* ================= UPDATE ================= */

    public String updateAttribute(Long id, AttributeDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("Attribute name cannot be empty");
        }

        String newName = dto.getName().trim();

        attributeRepository.findByNameAndCustomer_Id(newName, customerId)
                .filter(a -> !a.getId().equals(id))
                .ifPresent(a -> {
                    throw new RuntimeException("Another attribute with this name already exists.");
                });

        attribute.setName(newName);

        attributeRepository.save(attribute);

        return "Attribute updated successfully";
    }

    /* ================= DELETE ================= */

    public String deleteAttribute(Long id) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        attributeRepository.delete(attribute);

        return "Attribute deleted successfully";
    }
   
    @Transactional(readOnly = true)
    public List<AttributeReportDTO> getAttributesWithValues() {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<Attribute> attributes = attributeRepository.findByCustomer_Id(customerId);

        return attributes.stream().map(attribute -> {

            List<AttributeValue> values =
                    attributeValueRepository.findByAttribute_IdAndCustomer_Id(
                            attribute.getId(), customerId);

            List<String> valueNames = values.stream()
                    .map(AttributeValue::getValue)
                    .toList();

            AttributeReportDTO dto = new AttributeReportDTO();
            dto.setAttributeName(attribute.getName());
            dto.setValues(valueNames);

            return dto;

        }).toList();
    }
}