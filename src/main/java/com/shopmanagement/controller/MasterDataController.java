package com.shopmanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopmanagement.dto.AttributeDTO;
import com.shopmanagement.dto.AttributeReportDTO;
import com.shopmanagement.dto.AttributeResponseDTO;
import com.shopmanagement.dto.AttributeValueDTO;
import com.shopmanagement.dto.BrandDTO;
import com.shopmanagement.dto.CategoryDTO;
import com.shopmanagement.model.AttributeValue;
import com.shopmanagement.model.Brand;
import com.shopmanagement.model.Category;
import com.shopmanagement.service.AttributeService;
import com.shopmanagement.service.AttributeValueService;
import com.shopmanagement.service.MasterDataService;

@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final MasterDataService masterDataService;
    private final AttributeService attributeService ;
    private final AttributeValueService attributeValueService;

    public MasterDataController(MasterDataService masterDataService,AttributeService attributeService,AttributeValueService attributeValueService) {
        this.masterDataService = masterDataService;
		this.attributeService = attributeService;
		this.attributeValueService = attributeValueService;
    }

    /* =========================================================
       CATEGORY
       ========================================================= */

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>>getCategories() {
        return ResponseEntity.ok(masterDataService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(masterDataService.addCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) {
        masterDataService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    /* =========================================================
       BRAND
       ========================================================= */

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDTO>>getBrands() {
        return ResponseEntity.ok(masterDataService.getAllBrands());
    }

    @PostMapping("/brands")
    public ResponseEntity<String> createBrand(@RequestBody Brand brand) {
        return ResponseEntity.ok(masterDataService.addBrand(brand));
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable("id") Long id) {
        masterDataService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully");
    }

    /* =========================================================
       ATTRIBUTE
       ========================================================= */

    @GetMapping("/attributes")
    public ResponseEntity<List<AttributeResponseDTO>> getAttributes() {
        return ResponseEntity.ok(attributeService.getAllAttributes());
    }

    @PostMapping("/attributes")
    public ResponseEntity<Map<String, Object>> createAttribute(@RequestBody AttributeDTO attribute) {
        return ResponseEntity.ok(attributeService.createAttribute(attribute));
    }

    @DeleteMapping("/attributes/{id}")
    public ResponseEntity<String> deleteAttribute(@PathVariable Long id) {
    	attributeService.deleteAttribute(id);
        return ResponseEntity.ok("Attribute deleted successfully");
    }

    /* =========================================================
       ATTRIBUTE VALUES
       ========================================================= */

    @GetMapping("/attribute-values/{attributeId}")
    public ResponseEntity<List<AttributeValue>> getValuesByAttribute(
            @PathVariable Long attributeId) {

        return ResponseEntity.ok(
        		attributeValueService.getValuesByAttribute(attributeId)
        );
    }

    @PostMapping("/attribute-values")
    public ResponseEntity<String> createAttributeValue(
            @RequestBody AttributeValueDTO value) {

        return ResponseEntity.ok(
        		attributeValueService.createAttributeValues(value)
        );
    }

    @DeleteMapping("/attribute-values/{id}")
    public ResponseEntity<String> deleteAttributeValue(@PathVariable Long id) {
    	attributeValueService.deleteAttributeValue(id);
        return ResponseEntity.ok("Attribute value deleted successfully");
    }
    
    @GetMapping("/attributes-with-values")
    public ResponseEntity<List<AttributeReportDTO>> getAttributesWithValues() {
        return ResponseEntity.ok(
                attributeService.getAttributesWithValues()
        );
    }
    
}