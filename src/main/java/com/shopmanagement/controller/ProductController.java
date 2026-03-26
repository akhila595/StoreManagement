package com.shopmanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shopmanagement.dto.ProductDTO;
import com.shopmanagement.dto.ProductVariantDTO;
import com.shopmanagement.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;
	

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	/*
	 * ========================================================= CREATE PRODUCT
	 * (With Image + Attributes)
	 * =========================================================
	 */

	@PostMapping
	public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductDTO productDTO) {

		return ResponseEntity.ok(productService.createProduct(productDTO));
	}

	/*
	 * ========================================================= GET ALL PRODUCTS
	 * =========================================================
	 */

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	/*
	 * ========================================================= GET PRODUCT BY ID
	 * =========================================================
	 */

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getProductById(@PathVariable("id") Long id) {

		return ResponseEntity.ok(productService.getProductById(id));
	}

	/*
	 * ========================================================= UPDATE PRODUCT
	 * =========================================================
	 */

	@PutMapping(value = "/{id}")
	public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable("id") Long id,
			@RequestBody ProductDTO productDTO) {

		return ResponseEntity.ok(productService.updateProduct(id, productDTO));
	}

	/*
	 * ========================================================= DELETE PRODUCT
	 * (SOFT DELETE) =========================================================
	 */

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable("id") Long id) {

		return ResponseEntity.ok(productService.deleteProduct(id));
	}
	
	@PostMapping("/uploads/temp-image")
	public Map<String, Object> uploadTempImage(@RequestParam("file") MultipartFile file) {

	    String tempPath = productService.saveTempImage(file);

	    return Map.of(
	            "message", "Image uploaded",
	            "tempPath", tempPath
	    );
	}
	
	@GetMapping("/variants/{productId}")
	public ResponseEntity<List<ProductVariantDTO>> getVariants(
	        @PathVariable("productId") Long productId) {

	    return ResponseEntity.ok(
	    		productService.getVariantsByProduct(productId)
	    );
	}
}