package com.shopmanagement.controller;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /* =========================================================
       CREATE PRODUCT (With Image + Attributes)
       ========================================================= */

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(
                productService.createProduct(productDTO, imageFile)
        );
    }

    /* =========================================================
       GET ALL PRODUCTS
       ========================================================= */

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /* =========================================================
       GET PRODUCT BY ID
       ========================================================= */

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    /* =========================================================
       UPDATE PRODUCT
       ========================================================= */

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(
                productService.updateProduct(id, productDTO, imageFile)
        );
    }

    /* =========================================================
       DELETE PRODUCT (SOFT DELETE)
       ========================================================= */

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.deleteProduct(id)
        );
    }
}