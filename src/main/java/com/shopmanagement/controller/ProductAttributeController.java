package com.shopmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopmanagement.dto.AttributeDTO;
import com.shopmanagement.service.ProductAttributeService;

@RestController
@RequestMapping("/api/product-attributes")
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<AttributeDTO>> getProductAttributes(
            @PathVariable("productId") Long productId) {

    	 return ResponseEntity.ok(
                 productAttributeService.getAttributesOfProduct(productId)
         );
    }
}