package com.shopmanagement.service;

import com.shopmanagement.dto.AttributeDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttributeService {

    @Autowired private AttributeRepository attributeRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private JwtUtils jwtUtils;

    /* ================= CREATE ================= */

    public Map<String, Object> createAttribute(AttributeDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        attributeRepository.findByNameAndCustomer_Id(dto.getName(), customerId)
                .ifPresent(a -> {
                    throw new RuntimeException("Attribute already exists.");
                });

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Attribute attribute = new Attribute();
        attribute.setName(dto.getName());
        attribute.setCustomer(customer);

        Attribute saved = attributeRepository.save(attribute);

        return Map.of("message", "Attribute created",
                "data", saved);
    }

    /* ================= READ ALL ================= */

    public List<Attribute> getAllAttributes() {
        Long customerId = jwtUtils.getRequiredCustomerId();
        return attributeRepository.findByCustomer_Id(customerId);
    }

    /* ================= UPDATE ================= */

    public String updateAttribute(Long id, AttributeDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        attribute.setName(dto.getName());
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
}