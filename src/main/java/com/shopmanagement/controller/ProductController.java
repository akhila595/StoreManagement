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
}