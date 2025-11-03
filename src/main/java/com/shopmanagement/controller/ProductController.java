package com.shopmanagement.controller;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // --- CREATE Product ---
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    // --- READ All Products ---
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // --- READ Product by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // --- READ Product by Design Code ---
    @GetMapping("/design/{designCode}")
    public ResponseEntity<Map<String, Object>> getProductByDesignCode(@PathVariable("designCode") String designCode) {
        return ResponseEntity.ok(productService.getProductByDesignCode(designCode));
    }

    // --- UPDATE Product ---
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    // --- DELETE Product ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
