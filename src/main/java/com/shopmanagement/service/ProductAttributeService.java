package com.shopmanagement.service;

import com.shopmanagement.dto.AttributeDTO;
import com.shopmanagement.dto.ProductAttributeDTO;
import com.shopmanagement.model.*;
import com.shopmanagement.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductAttributeService {

    @Autowired private ProductRepository productRepository;
    @Autowired private AttributeRepository attributeRepository;
    @Autowired private ProductAttributeRepository productAttributeRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private JwtUtils jwtUtils;

    /* ================= ASSIGN ATTRIBUTE TO PRODUCT ================= */

    public String assignAttributeToProduct(ProductAttributeDTO dto) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        Product product = productRepository
                .findByProductIdAndCustomer_Id(dto.getProductId(), customerId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Attribute attribute = attributeRepository
                .findByIdAndCustomer_Id(dto.getAttributeId(), customerId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        // Prevent duplicate link
        productAttributeRepository
                .findByProduct_ProductIdAndAttribute_IdAndCustomer_Id(
                        dto.getProductId(),
                        dto.getAttributeId(),
                        customerId)
                .ifPresent(pa -> {
                    throw new RuntimeException("Attribute already assigned to product.");
                });

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ProductAttribute pa = new ProductAttribute();
        pa.setProduct(product);
        pa.setAttribute(attribute);
        pa.setCustomer(customer);

        productAttributeRepository.save(pa);

        return "Attribute assigned to product successfully";
    }

    /* ================= GET PRODUCT ATTRIBUTES ================= */

    public List<AttributeDTO> getAttributesOfProduct(Long productId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        List<ProductAttribute> list =
                productAttributeRepository
                        .findByProduct_productIdAndCustomer_Id(productId, customerId);

        List<AttributeDTO> result = new ArrayList<>();

        for (ProductAttribute pa : list) {

            Attribute attr = pa.getAttribute();

            AttributeDTO dto = new AttributeDTO();
            dto.setId(attr.getId());
            dto.setName(attr.getName());

            result.add(dto);
        }
        return result; 
        
    }

    /* ================= REMOVE ATTRIBUTE FROM PRODUCT ================= */

    public String removeAttributeFromProduct(Long productAttributeId) {

        Long customerId = jwtUtils.getRequiredCustomerId();

        ProductAttribute pa =
                productAttributeRepository
                        .findByIdAndCustomer_Id(productAttributeId, customerId)
                        .orElseThrow(() -> new RuntimeException("Product attribute not found"));

        productAttributeRepository.delete(pa);

        return "Attribute removed from product successfully";
    }
}